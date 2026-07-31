package az.less.feature.transfer.impl.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import az.less.core.di.api
import az.less.feature.chat.api.model.WidgetMessage
import az.less.feature.chat.api.widget.ChatWidgetComposer
import az.less.feature.chat.api.widget.ChatWidgetViewHolder
import az.less.feature.chat.api.widget.ChatWidgetViewHolderFactory
import az.less.feature.chat.api.widget.findFragmentActivity
import az.less.feature.transfer.api.TransferFeatureApi
import az.less.feature.transfer.impl.R

/**
 * Виджет «быстрый перевод» — вклад целиком server-driven фичи transfer в чат:
 * кнопка запускает её SDUI-флоу через собственный лаунчер. Сумма-подсказка
 * приходит в payload (server-driven данные виджета).
 */

/** XML-рендер. */
internal class TransferActionViewHolderFactory : ChatWidgetViewHolderFactory {

    override fun create(parent: ViewGroup): ChatWidgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_widget_transfer, parent, false)
        return object : ChatWidgetViewHolder(view) {
            private val amount = view.findViewById<TextView>(R.id.amount)
            private val transfer = view.findViewById<Button>(R.id.transfer)

            override fun bind(message: WidgetMessage) {
                amount.text = "${message.payload["amount"] ?: "100"} ₽"
                transfer.setOnClickListener { view ->
                    view.context.findFragmentActivity()?.let {
                        api<TransferFeatureApi>().launcher().launch(it.supportFragmentManager)
                    }
                }
            }
        }
    }
}

/** Compose-рендер того же виджета. */
internal class TransferActionComposer : ChatWidgetComposer {

    @Composable
    override fun Content(message: WidgetMessage) {
        val context = LocalContext.current
        Column {
            Text("Быстрый перевод", style = MaterialTheme.typography.labelSmall)
            Text("${message.payload["amount"] ?: "100"} ₽", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = {
                context.findFragmentActivity()?.let {
                    api<TransferFeatureApi>().launcher().launch(it.supportFragmentManager)
                }
            }) {
                Text("Перевести (SDUI)")
            }
        }
    }
}
