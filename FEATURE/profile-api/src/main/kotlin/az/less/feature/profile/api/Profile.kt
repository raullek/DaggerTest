package az.less.feature.profile.api

/** Доменная модель профиля. Публичный контракт фичи (api-слой). */
data class Profile(
    val name: String,
    val email: String,
)
