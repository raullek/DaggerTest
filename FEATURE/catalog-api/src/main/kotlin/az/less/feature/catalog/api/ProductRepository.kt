package az.less.feature.catalog.api

interface ProductRepository {
    suspend fun getProducts(): List<Product>
}
