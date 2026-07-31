package az.less.feature.catalog.impl.presentation

import az.less.feature.catalog.api.Product
import az.less.feature.catalog.api.ProductRepository

internal class CatalogViewModel(
    private val repository: ProductRepository,
) {
    suspend fun load(): List<Product> = repository.getProducts()
}
