package az.less.feature.profile.api

/**
 * Доменный контракт. Реализация (на Retrofit) живёт в impl-модуле и не видна наружу.
 */
interface ProfileRepository {
    suspend fun getProfile(): Profile
}
