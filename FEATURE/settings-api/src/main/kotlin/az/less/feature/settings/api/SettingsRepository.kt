package az.less.feature.settings.api

interface SettingsRepository {
    suspend fun getSettings(): AppSettings
    fun toggleDarkTheme(): AppSettings
}
