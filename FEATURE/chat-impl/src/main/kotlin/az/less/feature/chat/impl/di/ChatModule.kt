package az.less.feature.chat.impl.di

import az.less.core.di.PerFeature
import az.less.feature.chat.api.ChatLauncher
import az.less.feature.chat.api.action.ChatQuickAction
import az.less.feature.chat.api.widget.ChatWidgetComposer
import az.less.feature.chat.api.widget.ChatWidgets
import az.less.feature.chat.api.widget.ChatWidgetViewHolderFactory
import az.less.feature.chat.impl.data.ChatRepositoryImpl
import az.less.feature.chat.impl.domain.ChatRepository
import az.less.feature.chat.impl.domain.ChatWidgetRegistry
import az.less.feature.chat.impl.navigation.ChatLauncherImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds

/**
 * Внутренние биндинги фичи чата. Модуль ПУБЛИЧНЫЙ: его включает `ChatComponent`,
 * объявленный в app-модуле.
 *
 * Здесь же @Multibinds-швы вкладов: карты/набор существуют, даже если ни одна фича
 * ничего не вложила (идиома пустых @Multibinds).
 */
@Module
interface ChatModule {

    @Binds
    @PerFeature
    fun bindLauncher(impl: ChatLauncherImpl): ChatLauncher

    @Binds
    @PerFeature
    fun bindRepository(impl: ChatRepositoryImpl): ChatRepository

    /** Гарантирует существование карты XML-фабрик (возможно пустой). */
    @Multibinds
    @ChatWidgets
    fun xmlWidgetFactories(): Map<String, @JvmSuppressWildcards ChatWidgetViewHolderFactory>

    /** Гарантирует существование карты Compose-рендеров (возможно пустой). */
    @Multibinds
    @ChatWidgets
    fun widgetComposers(): Map<String, @JvmSuppressWildcards ChatWidgetComposer>

    /** Гарантирует существование набора быстрых действий (возможно пустого). */
    @Multibinds
    fun quickActions(): Set<@JvmSuppressWildcards ChatQuickAction>

    companion object {
        /** Единственное место, где «сырые» агрегаты заворачиваются в реестр-контейнер. */
        @Provides
        @PerFeature
        fun provideWidgetRegistry(
            @ChatWidgets xmlFactories: Map<String, @JvmSuppressWildcards ChatWidgetViewHolderFactory>,
            @ChatWidgets composers: Map<String, @JvmSuppressWildcards ChatWidgetComposer>,
            quickActions: Set<@JvmSuppressWildcards ChatQuickAction>,
        ): ChatWidgetRegistry = ChatWidgetRegistry(xmlFactories, composers, quickActions)
    }
}
