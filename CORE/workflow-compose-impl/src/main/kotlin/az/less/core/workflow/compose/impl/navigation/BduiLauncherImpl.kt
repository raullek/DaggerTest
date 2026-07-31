package az.less.core.workflow.compose.impl.navigation

import androidx.fragment.app.FragmentManager
import az.less.core.workflow.compose.api.navigation.BduiLauncher
import az.less.core.workflow.compose.impl.ui.WorkflowComposeFragment
import javax.inject.Inject

/**
 * Запуск BDUI-флоу: поднимает [WorkflowComposeFragment] поверх текущего экрана. Стиль навигации —
 * как у фич-лаунчеров проекта.
 */
internal class BduiLauncherImpl @Inject constructor() : BduiLauncher {

    override fun launch(fragmentManager: FragmentManager, flow: String) {
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, WorkflowComposeFragment.create(flow))
            .addToBackStack("bdui:$flow")
            .commit()
    }
}
