package az.less.feature.settings.impl.di

import az.less.core.di.api
import az.less.feature.chat.api.action.ChatQuickAction
import az.less.feature.settings.api.SettingsFeatureApi
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

/**
 * Вклад фичи settings в чат: ТОЛЬКО быстрое действие (@IntoSet), без виджета —
 * демонстрация, что вклады независимы: фича участвует лишь в тех агрегатах,
 * которые ей нужны (Set-вклад не требует даже Compose-зависимостей).
 */
@Module
interface SettingsChatActionsModule {

    companion object {
        @Provides
        @IntoSet
        fun provideSettingsQuickAction(): ChatQuickAction = ChatQuickAction(
            id = "settings",
            title = "Настройки",
            order = 40,
            onClick = { activity ->
                api<SettingsFeatureApi>().launcher().launch(activity.supportFragmentManager)
            },
        )
    }
}
