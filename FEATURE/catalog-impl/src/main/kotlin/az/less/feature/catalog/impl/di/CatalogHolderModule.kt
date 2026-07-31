package az.less.feature.catalog.impl.di

import az.less.core.di.FeatureContainer
import az.less.core.di.FeatureHolder
import az.less.feature.catalog.api.CatalogFeatureApi
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
interface CatalogHolderModule {

    companion object {
        @Provides
        @Singleton
        @IntoMap
        @ClassKey(CatalogFeatureApi::class)
        fun provideCatalogHolder(container: FeatureContainer): FeatureHolder<*> =
            CatalogHolder(container)
    }
}
