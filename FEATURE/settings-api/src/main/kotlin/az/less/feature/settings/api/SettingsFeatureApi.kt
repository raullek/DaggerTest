package az.less.feature.settings.api

import az.less.core.di.FeatureApi

interface SettingsFeatureApi : FeatureApi {
    fun launcher(): SettingsLauncher
    fun settingsRepository(): SettingsRepository
}
