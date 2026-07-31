package az.less.core.workflow.compose.api.navigation

import androidx.fragment.app.FragmentManager

/**
 * Запуск BDUI-флоу из любой фичи: поднимает Compose-хост поверх текущего экрана.
 */
interface BduiLauncher {
    fun launch(fragmentManager: FragmentManager, flow: String)
}
