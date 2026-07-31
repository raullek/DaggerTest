package az.less.feature.settings.impl.presentation

import az.less.feature.settings.api.AppSettings
import az.less.feature.settings.api.SettingsRepository

internal class SettingsViewModel(
    private val repository: SettingsRepository,
) {
    suspend fun load(): AppSettings = repository.getSettings()
    fun toggleDarkTheme(): AppSettings = repository.toggleDarkTheme()
}
