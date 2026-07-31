package az.less.feature.catalog.api

import az.less.core.di.FeatureApi

interface CatalogFeatureApi : FeatureApi {
    fun launcher(): CatalogLauncher
    fun productRepository(): ProductRepository
}
