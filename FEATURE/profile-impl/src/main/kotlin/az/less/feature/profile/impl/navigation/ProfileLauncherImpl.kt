package az.less.feature.profile.impl.navigation

import androidx.fragment.app.FragmentManager
import az.less.feature.profile.api.ProfileLauncher
import az.less.feature.profile.impl.presentation.ProfileFragment
import javax.inject.Inject

/**
 * Стартовая логика фичи profile. Инжектится в граф (@Binds в ProfileModule) и
 * отдаётся наружу через ProfileFeatureApi.launcher(). Сам решает, как открыть
 * свой экран — кладёт ProfileFragment в корневой контейнер Activity.
 */
internal class ProfileLauncherImpl @Inject constructor() : ProfileLauncher {
    override fun launch(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, ProfileFragment())
            .addToBackStack("profile")
            .commit()
    }
}
