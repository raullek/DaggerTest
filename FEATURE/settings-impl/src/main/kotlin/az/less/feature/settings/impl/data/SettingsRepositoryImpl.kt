package az.less.feature.settings.impl.data

import az.less.feature.settings.api.AppSettings
import az.less.feature.settings.api.SettingsRepository
import javax.inject.Inject

/** Скоуп задаётся @PerFeature на @Binds в SettingsModule — состояние живёт в графе фичи. */
internal class SettingsRepositoryImpl @Inject constructor(
    private val service: SettingsService,
) : SettingsRepository {

    private var current = AppSettings(darkTheme = false, notificationsEnabled = true)

    override suspend fun getSettings(): AppSettings {
        current = try {
            val dto = service.fetchSettings()
            AppSettings(dto.darkTheme ?: false, dto.notificationsEnabled ?: true)
        } catch (e: Exception) {
            current // оффлайн — текущее локальное состояние
        }
        return current
    }

    override fun toggleDarkTheme(): AppSettings {
        current = current.copy(darkTheme = !current.darkTheme)
        return current
    }
}
