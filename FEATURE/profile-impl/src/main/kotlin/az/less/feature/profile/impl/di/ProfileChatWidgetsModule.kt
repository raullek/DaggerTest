package az.less.feature.profile.impl.di

import az.less.core.di.api
import az.less.feature.chat.api.action.ChatQuickAction
import az.less.feature.chat.api.widget.ChatWidgetComposer
import az.less.feature.chat.api.widget.ChatWidgetIds
import az.less.feature.chat.api.widget.ChatWidgets
import az.less.feature.chat.api.widget.ChatWidgetViewHolderFactory
import az.less.feature.profile.api.ProfileFeatureApi
import az.less.feature.profile.impl.chat.ProfileCardComposer
import az.less.feature.profile.impl.chat.ProfileCardViewHolderFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dagger.multibindings.StringKey

/**
 * Вклад фичи profile в чат: оба рендера виджета `PROFILE_CARD` (@IntoMap по одному ключу
 * в две карты) + быстрое действие (@IntoSet). Модуль живёт в impl фичи-поставщика,
 * а подключается через `app/CompoundChatWidgetsModule`.
 */
@Module
interface ProfileChatWidgetsModule {

    companion object {
        @Provides
        @IntoMap
        @ChatWidgets
        @StringKey(ChatWidgetIds.PROFILE_CARD)
        fun provideProfileCardXmlFactory(): ChatWidgetViewHolderFactory = ProfileCardViewHolderFactory()

        @Provides
        @IntoMap
        @ChatWidgets
        @StringKey(ChatWidgetIds.PROFILE_CARD)
        fun provideProfileCardComposer(): ChatWidgetComposer = ProfileCardComposer()

        /** «Визитка» фичи в ряду чипов. */
        @Provides
        @IntoSet
        fun provideProfileQuickAction(): ChatQuickAction = ChatQuickAction(
            id = "profile",
            title = "Профиль",
            order = 10,
            onClick = { activity ->
                api<ProfileFeatureApi>().launcher().launch(activity.supportFragmentManager)
            },
        )
    }
}
