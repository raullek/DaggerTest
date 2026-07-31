package az.less.feature.catalog.impl.di

import az.less.core.di.BaseFeatureHolder
import az.less.core.di.FeatureContainer
import az.less.core.network.api.NetworkCoreApi
import az.less.feature.catalog.api.CatalogFeatureApi

internal class CatalogHolder(
    container: FeatureContainer,
) : BaseFeatureHolder<CatalogFeatureApi>(container) {

    override fun buildFeature(): CatalogFeatureApi =
        DaggerCatalogComponent.factory()
            .create(getDependency(NetworkCoreApi::class.java))
}
