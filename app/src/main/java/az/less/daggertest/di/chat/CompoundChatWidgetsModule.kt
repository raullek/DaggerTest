package az.less.daggertest.di.chat

import az.less.feature.catalog.impl.di.CatalogChatWidgetsModule
import az.less.feature.profile.impl.di.ProfileChatWidgetsModule
import az.less.feature.settings.impl.di.SettingsChatActionsModule
import az.less.feature.transfer.impl.di.TransferChatWidgetsModule
import dagger.Module

/**
 * Композитный модуль, собирающий вклады всех фич в чат:
 * app-модуль — ЕДИНСТВЕННОЕ место, знающее полный список поставщиков,
 * сами вклады живут в impl фич-владельцев. Включается в `modules` [ChatComponent]
 * (не в корневой AppComponent!) — мультибиндинги собираются в графе самой фичи.
 *
 * Чтобы добавить виджет своей фичи в чат, создайте в её impl модуль с кодом:
 * <pre>
 *     @Provides @IntoMap @ChatWidgets @StringKey(ChatWidgetIds.ВАШ_ВИДЖЕТ)
 *     fun provideXmlFactory(): ChatWidgetViewHolderFactory = ...
 *
 *     @Provides @IntoMap @ChatWidgets @StringKey(ChatWidgetIds.ВАШ_ВИДЖЕТ)
 *     fun provideComposer(): ChatWidgetComposer = ...
 *
 *     @Provides @IntoSet
 *     fun provideQuickAction(): ChatQuickAction = ...
 * </pre>
 * и добавьте его в includes ниже.
 */
@Module(
    includes = [
        ProfileChatWidgetsModule::class,
        CatalogChatWidgetsModule::class,
        TransferChatWidgetsModule::class,
        SettingsChatActionsModule::class,
    ],
)
interface CompoundChatWidgetsModule
