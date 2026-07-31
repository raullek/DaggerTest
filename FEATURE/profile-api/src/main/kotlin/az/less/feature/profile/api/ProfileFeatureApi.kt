package az.less.feature.profile.api

import az.less.core.di.FeatureApi

/**
 * Публичное API фичи profile. Отдаёт лаунчер (для перехода в фичу из других модулей)
 * и доменный репозиторий (реализация на Retrofit скрыта в impl).
 */
interface ProfileFeatureApi : FeatureApi {
    fun launcher(): ProfileLauncher
    fun profileRepository(): ProfileRepository
}
