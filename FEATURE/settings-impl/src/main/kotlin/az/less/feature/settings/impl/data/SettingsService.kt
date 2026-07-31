package az.less.feature.settings.impl.data

import retrofit2.http.GET

internal interface SettingsService {
    @GET("settings")
    suspend fun fetchSettings(): SettingsDto
}

internal data class SettingsDto(
    val darkTheme: Boolean?,
    val notificationsEnabled: Boolean?,
)
