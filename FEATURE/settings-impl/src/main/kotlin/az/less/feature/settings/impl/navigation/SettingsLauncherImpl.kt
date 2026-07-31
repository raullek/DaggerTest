package az.less.feature.settings.impl.navigation

import androidx.fragment.app.FragmentManager
import az.less.feature.settings.api.SettingsLauncher
import az.less.feature.settings.impl.presentation.SettingsFragment
import javax.inject.Inject

internal class SettingsLauncherImpl @Inject constructor() : SettingsLauncher {
    override fun launch(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .addToBackStack("settings")
            .commit()
    }
}
