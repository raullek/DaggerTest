package az.less.core.workflow.impl.navigation

import androidx.fragment.app.FragmentManager
import az.less.core.workflow.api.navigation.WorkflowLauncher
import az.less.core.workflow.impl.ui.WorkflowHostFragment
import javax.inject.Inject

/**
 * Запуск флоу: поднимает [WorkflowHostFragment] поверх текущего экрана.
 * Стиль навигации — как у фич-лаунчеров проекта.
 */
internal class WorkflowLauncherImpl @Inject constructor() : WorkflowLauncher {

    override fun launch(fragmentManager: FragmentManager, flow: String) {
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, WorkflowHostFragment.create(flow))
            .addToBackStack("workflow:$flow")
            .commit()
    }
}
