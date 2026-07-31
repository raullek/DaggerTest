package az.less.feature.profile.impl.presentation

import az.less.feature.profile.api.Profile
import az.less.feature.profile.api.ProfileRepository

/** Простой state-holder. Репозиторий приходит из ProfileFeatureApi (через DI). */
internal class ProfileViewModel(
    private val repository: ProfileRepository,
) {
    suspend fun load(): Profile = repository.getProfile()
}
