package az.less.feature.transfer.impl.di

import az.less.core.di.api
import az.less.feature.chat.api.action.ChatQuickAction
import az.less.feature.chat.api.widget.ChatWidgetComposer
import az.less.feature.chat.api.widget.ChatWidgetIds
import az.less.feature.chat.api.widget.ChatWidgets
import az.less.feature.chat.api.widget.ChatWidgetViewHolderFactory
import az.less.feature.transfer.api.TransferFeatureApi
import az.less.feature.transfer.impl.chat.TransferActionComposer
import az.less.feature.transfer.impl.chat.TransferActionViewHolderFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dagger.multibindings.StringKey

/** Вклад фичи transfer в чат: виджет `TRANSFER_ACTION` (оба рендера) + быстрое действие. */
@Module
interface TransferChatWidgetsModule {

    companion object {
        @Provides
        @IntoMap
        @ChatWidgets
        @StringKey(ChatWidgetIds.TRANSFER_ACTION)
        fun provideTransferActionXmlFactory(): ChatWidgetViewHolderFactory = TransferActionViewHolderFactory()

        @Provides
        @IntoMap
        @ChatWidgets
        @StringKey(ChatWidgetIds.TRANSFER_ACTION)
        fun provideTransferActionComposer(): ChatWidgetComposer = TransferActionComposer()

        @Provides
        @IntoSet
        fun provideTransferQuickAction(): ChatQuickAction = ChatQuickAction(
            id = "transfer",
            title = "Перевод",
            order = 30,
            onClick = { activity ->
                api<TransferFeatureApi>().launcher().launch(activity.supportFragmentManager)
            },
        )
    }
}
