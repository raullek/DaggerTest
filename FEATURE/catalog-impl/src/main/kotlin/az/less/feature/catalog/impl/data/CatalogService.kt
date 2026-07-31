package az.less.feature.catalog.impl.data

import retrofit2.http.GET

internal interface CatalogService {
    @GET("products")
    suspend fun fetchProducts(): List<ProductDto>
}

internal data class ProductDto(
    val title: String?,
    val price: Int?,
)
