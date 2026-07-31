package az.less.feature.profile.api

import androidx.fragment.app.FragmentManager

/**
 * Собственный лаунчер фичи profile (своя точка входа, без общего модуля навигации).
 * Реализация со стартовой логикой — в :impl (ProfileLauncherImpl).
 */
interface ProfileLauncher {
    fun launch(fragmentManager: FragmentManager)
}
