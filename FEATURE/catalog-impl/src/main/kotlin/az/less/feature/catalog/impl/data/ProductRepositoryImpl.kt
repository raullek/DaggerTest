package az.less.feature.catalog.impl.data

import az.less.feature.catalog.api.Product
import az.less.feature.catalog.api.ProductRepository
import javax.inject.Inject

internal class ProductRepositoryImpl @Inject constructor(
    private val service: CatalogService,
) : ProductRepository {

    override suspend fun getProducts(): List<Product> = try {
        service.fetchProducts().map { Product(it.title.orEmpty(), it.price ?: 0) }
    } catch (e: Exception) {
        // Заглушка для демо без бэкенда.
        listOf(
            Product("Дебетовая карта", 0),
            Product("Кредит наличными", 1990),
            Product("Вклад «Накопительный»", 0),
            Product("Инвестиции", 499),
        )
    }
}
