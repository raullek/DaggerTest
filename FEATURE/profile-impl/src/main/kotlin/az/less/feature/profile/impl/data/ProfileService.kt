package az.less.feature.profile.impl.data

import retrofit2.http.GET

/** Retrofit API профиля. Создаётся из инжектируемого Retrofit (см. ProfileModule). */
internal interface ProfileService {
    @GET("profile")
    suspend fun fetchProfile(): ProfileDto
}

internal data class ProfileDto(
    val name: String?,
    val email: String?,
)
