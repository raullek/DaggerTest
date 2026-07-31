package az.less.core.workflow.compose.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import az.less.core.di.api
import az.less.core.workflow.compose.api.WorkflowComposeFeatureApi

/**
 * Хост BDUI-флоу: Fragment с [ComposeView]. Берёт ядро через `api<WorkflowComposeFeatureApi>()`,
 * создаёт свежий движок на этот запуск и рендерит [WorkflowHost]. Завершение/фатал → `popBackStack`.
 */
class WorkflowComposeFragment : Fragment() {

    private val flow: String get() = requireArguments().getString(ARG_FLOW).orEmpty()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val core = api<WorkflowComposeFeatureApi>()
        val stateMachine = core.newStateMachine()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                az.less.core.designsystem.compose.theme.DsTheme {
                    WorkflowHost(
                        stateMachine = stateMachine,
                        reflector = core.reflector(),
                        formatters = core.formatters(),
                        strategyFactory = core.strategyFactory(),
                        flow = flow,
                        onExit = { parentFragmentManager.popBackStack() },
                        onMessage = { text ->
                            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
                        },
                    )
                }
            }
        }
    }

    companion object {
        private const val ARG_FLOW = "flow"

        fun create(flow: String): WorkflowComposeFragment = WorkflowComposeFragment().apply {
            arguments = Bundle().apply { putString(ARG_FLOW, flow) }
        }
    }
}
