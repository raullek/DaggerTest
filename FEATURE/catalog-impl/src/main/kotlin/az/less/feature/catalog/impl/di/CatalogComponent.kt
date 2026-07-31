package az.less.feature.catalog.impl.di

import az.less.core.di.PerFeature
import az.less.core.network.api.NetworkCoreApi
import az.less.feature.catalog.api.CatalogFeatureApi
import dagger.Component

@PerFeature
@Component(
    dependencies = [NetworkCoreApi::class],
    modules = [CatalogModule::class],
)
internal interface CatalogComponent : CatalogFeatureApi {

    @Component.Factory
    interface Factory {
        fun create(networkCoreApi: NetworkCoreApi): CatalogComponent
    }
}
