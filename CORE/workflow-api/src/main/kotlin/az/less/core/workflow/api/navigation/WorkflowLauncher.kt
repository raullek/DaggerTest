package az.less.core.workflow.api.navigation

import androidx.fragment.app.FragmentManager

/**
 * Точка запуска server-driven флоу из любой фичи.
 * Поднимает хост-экран, который сам поднимет движок и отрисует приходящие экраны.
 */
interface WorkflowLauncher {

    /** Открыть флоу с именем [flow] в контейнере [fragmentManager]. */
    fun launch(fragmentManager: FragmentManager, flow: String)
}
