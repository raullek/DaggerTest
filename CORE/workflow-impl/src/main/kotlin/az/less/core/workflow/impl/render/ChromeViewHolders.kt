package az.less.core.workflow.impl.render

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import az.less.core.designsystem.DesignSystemInflater
import az.less.core.designsystem.DsComponent
import az.less.core.designsystem.dsAction
import az.less.core.designsystem.dsRenderStepperDots
import az.less.core.designsystem.dsStepper
import az.less.core.designsystem.dsSubtitle
import az.less.core.designsystem.dsTitle
import az.less.core.workflow.api.widget.ScreenItem
import az.less.core.workflow.api.widget.WidgetViewHolder
import az.less.core.workflow.api.widget.WidgetViewHolderFactory
import az.less.core.workflow.api.widget.WorkflowInteraction
import javax.inject.Inject

/** Контейнер-обёртка под вьюхолдеры, чей конкретный компонент выбирается на bind (баннер/кнопка). */
internal fun itemContainer(parent: ViewGroup): ViewGroup = LinearLayout(parent.context).apply {
    orientation = LinearLayout.VERTICAL
    layoutParams = RecyclerView.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    )
}

/** Заголовок экрана (HEADER): title + subtitle. */
class HeaderViewHolder(itemView: View) : WidgetViewHolder(itemView) {
    override fun bind(item: ScreenItem, interaction: WorkflowInteraction) {
        itemView.dsTitle().text = item.title.orEmpty()
        itemView.dsSubtitle().apply {
            if (item.subtitle.isNullOrEmpty()) {
                visibility = View.GONE
            } else {
                text = item.subtitle
                visibility = View.VISIBLE
            }
        }
    }
}

class HeaderViewHolderFactory @Inject constructor() : WidgetViewHolderFactory {
    override fun create(parent: ViewGroup): WidgetViewHolder =
        HeaderViewHolder(DesignSystemInflater.inflate(parent.context, DsComponent.SECTION_HEADER, parent, false))
}

/** Индикатор шага (STEPPER): точки, активная — `step` (1-based). */
class StepperViewHolder(itemView: View) : WidgetViewHolder(itemView) {
    override fun bind(item: ScreenItem, interaction: WorkflowInteraction) {
        itemView.dsStepper().dsRenderStepperDots(steps = item.steps, activeIndex = item.step - 1)
    }
}

class StepperViewHolderFactory @Inject constructor() : WidgetViewHolderFactory {
    override fun create(parent: ViewGroup): WidgetViewHolder =
        StepperViewHolder(DesignSystemInflater.inflate(parent.context, DsComponent.STEPPER, parent, false))
}

/** Кнопка-событие (EVENT): primary/secondary по `event.isRollback`; дёргает [WorkflowInteraction]. */
class EventViewHolder(itemView: ViewGroup) : WidgetViewHolder(itemView) {
    override fun bind(item: ScreenItem, interaction: WorkflowInteraction) {
        val container = itemView as ViewGroup
        container.removeAllViews()
        val event = item.event ?: return
        val component = if (event.isRollback) DsComponent.BUTTON_SECONDARY else DsComponent.BUTTON
        val button = DesignSystemInflater.inflate(itemView.context, component, container, false).dsAction()
        button.text = event.title ?: event.name
        button.setOnClickListener {
            if (event.isRollback) interaction.rollback() else interaction.submit(event)
        }
        container.addView(button)
    }
}

class EventViewHolderFactory @Inject constructor() : WidgetViewHolderFactory {
    override fun create(parent: ViewGroup): WidgetViewHolder = EventViewHolder(itemContainer(parent))
}
