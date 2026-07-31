package az.less.core.workflow.impl.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import az.less.core.di.api
import az.less.core.workflow.api.WorkflowFeatureApi
import az.less.core.workflow.api.engine.WorkflowState
import az.less.core.workflow.api.engine.WorkflowStateMachine
import az.less.core.workflow.api.model.WorkflowEvent
import az.less.core.workflow.api.model.WorkflowReferences
import az.less.core.workflow.api.model.WorkflowResponse
import az.less.core.workflow.api.model.WorkflowScreen
import az.less.core.workflow.api.model.WorkflowWidget
import az.less.core.workflow.api.widget.ScreenItem
import az.less.core.workflow.api.widget.WidgetScope
import az.less.core.workflow.api.widget.WidgetViewHolderFactory
import az.less.core.workflow.api.widget.WorkflowInteraction
import az.less.core.workflow.impl.engine.WidgetScopeImpl
import az.less.core.workflow.impl.validation.FieldValidators
import kotlinx.coroutines.launch

/**
 * Хост server-driven флоу с тремя регионами (header/main/footer):
 * - **header** — закреплённая шапка (степпер + заголовок + `screen.header`-виджеты), не скроллится;
 * - **body** — основное тело (`screen.widgets`) в RecyclerView, скроллится;
 * - **footer** — закреплённый подвал (`screen.footer`-виджеты + кнопки-события), не скроллится.
 *
 * Все три региона рендерятся ОДНИМ реестром вьюхолдеров: body — через [WorkflowScreenAdapter],
 * header/footer — прямым создаванием вьюхолдеров фабриками (без RecyclerView, без рециклинга).
 * Реализует [WorkflowInteraction]: кнопка валидирует поля и шлёт событие/откат.
 */
class WorkflowHostFragment : Fragment(), WorkflowInteraction {

    private lateinit var stateMachine: WorkflowStateMachine
    private lateinit var factories: Map<String, WidgetViewHolderFactory>
    private lateinit var adapter: WorkflowScreenAdapter
    private lateinit var headerContainer: LinearLayout
    private lateinit var footerContainer: LinearLayout
    private lateinit var progress: ProgressBar

    private var currentScreen: WorkflowScreen? = null
    private var currentScope: WidgetScope? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val context = requireContext()
        val pad = (16 * resources.displayMetrics.density).toInt()

        headerContainer = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        footerContainer = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        val body = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            clipToPadding = false
        }
        factories = api<WorkflowFeatureApi>().widgetViewHolderFactories()
        adapter = WorkflowScreenAdapter(factories, this)
        body.adapter = adapter

        val screenRoot = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
            addView(headerContainer, lp(MATCH, WRAP))
            addView(body, LinearLayout.LayoutParams(MATCH, 0, 1f)) // вес — забирает всё свободное место
            addView(footerContainer, lp(MATCH, WRAP))
        }

        progress = ProgressBar(context).apply { visibility = View.GONE }
        return FrameLayout(context).apply {
            setBackgroundColor(android.graphics.Color.WHITE)
            addView(screenRoot, FrameLayout.LayoutParams(MATCH, MATCH))
            addView(progress, FrameLayout.LayoutParams(WRAP, WRAP).apply { gravity = Gravity.CENTER })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stateMachine = api<WorkflowFeatureApi>().newStateMachine()

        viewLifecycleOwner.lifecycleScope.launch { stateMachine.state.collect(::onState) }
        viewLifecycleOwner.lifecycleScope.launch {
            stateMachine.messages.collect { message ->
                toast(message.text)
                if (message.fatal) parentFragmentManager.popBackStack()
            }
        }
        stateMachine.start(requireArguments().getString(ARG_FLOW).orEmpty())
    }

    private fun onState(state: WorkflowState) {
        progress.visibility = if (state is WorkflowState.Loading) View.VISIBLE else View.GONE
        when (state) {
            WorkflowState.Idle, WorkflowState.Loading -> Unit
            is WorkflowState.Screen -> renderScreen(state.response)
            is WorkflowState.Finished -> {
                toast("Готово")
                parentFragmentManager.popBackStack()
            }
            is WorkflowState.Failed -> toast("Ошибка: ${state.message}")
        }
    }

    private fun renderScreen(response: WorkflowResponse) {
        val screen = response.screen ?: return
        val scope = WidgetScopeImpl(screen)
        currentScreen = screen
        currentScope = scope
        val refs = response.references

        // header: степпер + заголовок + закреплённые виджеты шапки
        val headerItems = mutableListOf<ScreenItem>()
        val step = screen.properties["step"]?.toIntOrNull()
        val steps = screen.properties["steps"]?.toIntOrNull()
        if (step != null && steps != null) headerItems += ScreenItem(ScreenItem.TYPE_STEPPER, step = step, steps = steps)
        headerItems += ScreenItem(ScreenItem.TYPE_HEADER, title = screen.title, subtitle = screen.description)
        headerItems += widgetItems(screen.header, refs, scope)
        renderRegion(headerContainer, headerItems)

        // body: основное тело (скроллится)
        adapter.submit(widgetItems(screen.widgets, refs, scope).filter { adapter.hasFactory(it.typeKey) })

        // footer: закреплённые виджеты подвала + кнопки-события
        val footerItems = widgetItems(screen.footer, refs, scope).toMutableList()
        response.events.filterNot { it.hidden }.forEach { footerItems += ScreenItem(ScreenItem.TYPE_EVENT, event = it) }
        renderRegion(footerContainer, footerItems)
    }

    private fun widgetItems(
        widgets: List<WorkflowWidget>,
        refs: WorkflowReferences,
        scope: WidgetScope,
    ): List<ScreenItem> = widgets.map { ScreenItem(it.type, widget = it, references = refs, scope = scope) }

    /** Рендер закреплённого региона: создаём вьюхолдеры теми же фабриками и кладём их itemView. */
    private fun renderRegion(container: LinearLayout, items: List<ScreenItem>) {
        container.removeAllViews()
        items.forEach { item ->
            val factory = factories[item.typeKey] ?: return@forEach
            val holder = factory.create(container)
            holder.bind(item, this)
            container.addView(holder.itemView)
        }
    }

    // --- WorkflowInteraction ---

    override fun submit(event: WorkflowEvent) {
        val screen = currentScreen ?: return
        val scope = currentScope ?: return
        if (validate(screen, scope)) stateMachine.sendEvent(event.name, scope.retrieveData())
    }

    override fun rollback() {
        stateMachine.rollback()
    }

    private fun validate(screen: WorkflowScreen, scope: WidgetScope): Boolean {
        var allValid = true
        screen.fields.forEach { field ->
            val result = FieldValidators.validate(field.validators, scope.valueHolder(field).get())
            scope.errorHolder(field).update(result.error)
            if (!result.valid) allValid = false
        }
        return allValid
    }

    private fun lp(w: Int, h: Int) = LinearLayout.LayoutParams(w, h)

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ARG_FLOW = "az.less.workflow.ARG_FLOW"
        private const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
        private const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

        fun create(flow: String): WorkflowHostFragment = WorkflowHostFragment().apply {
            arguments = Bundle().apply { putString(ARG_FLOW, flow) }
        }
    }
}
