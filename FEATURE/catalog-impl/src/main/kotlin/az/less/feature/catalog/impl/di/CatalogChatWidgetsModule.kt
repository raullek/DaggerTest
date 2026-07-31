package az.less.feature.catalog.impl.di

import az.less.core.di.api
import az.less.feature.catalog.api.CatalogFeatureApi
import az.less.feature.catalog.impl.chat.CatalogShowcaseComposer
import az.less.feature.catalog.impl.chat.CatalogShowcaseViewHolderFactory
import az.less.feature.chat.api.action.ChatQuickAction
import az.less.feature.chat.api.widget.ChatWidgetComposer
import az.less.feature.chat.api.widget.ChatWidgetIds
import az.less.feature.chat.api.widget.ChatWidgets
import az.less.feature.chat.api.widget.ChatWidgetViewHolderFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dagger.multibindings.StringKey

/** Вклад фичи catalog в чат: виджет `CATALOG_SHOWCASE` (оба рендера) + быстрое действие. */
@Module
interface CatalogChatWidgetsModule {

    companion object {
        @Provides
        @IntoMap
        @ChatWidgets
        @StringKey(ChatWidgetIds.CATALOG_SHOWCASE)
        fun provideCatalogShowcaseXmlFactory(): ChatWidgetViewHolderFactory = CatalogShowcaseViewHolderFactory()

        @Provides
        @IntoMap
        @ChatWidgets
        @StringKey(ChatWidgetIds.CATALOG_SHOWCASE)
        fun provideCatalogShowcaseComposer(): ChatWidgetComposer = CatalogShowcaseComposer()

        @Provides
        @IntoSet
        fun provideCatalogQuickAction(): ChatQuickAction = ChatQuickAction(
            id = "catalog",
            title = "Каталог",
            order = 20,
            onClick = { activity ->
                api<CatalogFeatureApi>().launcher().launch(activity.supportFragmentManager)
            },
        )
    }
}
