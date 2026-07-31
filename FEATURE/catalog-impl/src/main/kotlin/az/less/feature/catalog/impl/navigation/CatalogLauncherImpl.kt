package az.less.feature.catalog.impl.navigation

import androidx.fragment.app.FragmentManager
import az.less.feature.catalog.api.CatalogLauncher
import az.less.feature.catalog.impl.presentation.CatalogFragment
import javax.inject.Inject

internal class CatalogLauncherImpl @Inject constructor() : CatalogLauncher {
    override fun launch(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, CatalogFragment())
            .addToBackStack("catalog")
            .commit()
    }
}
