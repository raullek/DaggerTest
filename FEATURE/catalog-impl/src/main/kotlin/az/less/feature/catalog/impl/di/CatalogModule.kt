package az.less.feature.catalog.impl.di

import az.less.core.di.PerFeature
import az.less.feature.catalog.api.CatalogLauncher
import az.less.feature.catalog.api.ProductRepository
import az.less.feature.catalog.impl.data.CatalogService
import az.less.feature.catalog.impl.data.ProductRepositoryImpl
import az.less.feature.catalog.impl.navigation.CatalogLauncherImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
internal interface CatalogModule {

    @Binds
    @PerFeature
    fun bindLauncher(impl: CatalogLauncherImpl): CatalogLauncher

    @Binds
    fun bindRepository(impl: ProductRepositoryImpl): ProductRepository

    companion object {
        @Provides
        @PerFeature
        fun provideService(retrofit: Retrofit): CatalogService =
            retrofit.create(CatalogService::class.java)
    }
}
