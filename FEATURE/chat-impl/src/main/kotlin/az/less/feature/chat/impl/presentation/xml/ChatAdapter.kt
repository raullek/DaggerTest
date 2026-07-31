package az.less.feature.chat.impl.presentation.xml

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import az.less.feature.chat.api.model.ChatMessage
import az.less.feature.chat.api.model.TextMessage
import az.less.feature.chat.api.model.WidgetMessage
import az.less.feature.chat.api.widget.ChatWidgetViewHolder
import az.less.feature.chat.impl.R
import az.less.feature.chat.impl.domain.ChatWidgetRegistry

/**
 * Адаптер ленты чата. Ядро знает только ДВА своих типа ячеек (текст in/out) и фолбэк;
 * каждому известному server-driven виджету выдаётся собственный view type по позиции id
 * в реестре, а сама вьюха создаётся фабрикой фичи-поставщика из `@ChatWidgets Map`.
 */
internal class ChatAdapter(
    private val registry: ChatWidgetRegistry,
) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(Diff) {

    // Стабильный порядок известных виджетов → смещённые view types после базовых.
    private val widgetIds: List<String> = registry.xmlWidgetIds()

    override fun getItemViewType(position: Int): Int = when (val message = getItem(position)) {
        is TextMessage -> if (message.incoming) TYPE_TEXT_IN else TYPE_TEXT_OUT
        is WidgetMessage -> {
            val index = widgetIds.indexOf(message.widgetId)
            if (index >= 0) TYPE_WIDGET_BASE + index else TYPE_WIDGET_UNKNOWN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when {
            viewType == TYPE_TEXT_IN ->
                TextViewHolder(inflater.inflate(R.layout.item_chat_text_in, parent, false))

            viewType == TYPE_TEXT_OUT ->
                TextViewHolder(inflater.inflate(R.layout.item_chat_text_out, parent, false))

            viewType == TYPE_WIDGET_UNKNOWN ->
                UnknownWidgetViewHolder(inflater.inflate(R.layout.item_chat_widget_unknown, parent, false))

            else -> {
                // Вьюха виджета приходит из фабрики фичи-поставщика — ядро её содержимого не знает.
                val root = inflater.inflate(R.layout.item_chat_widget, parent, false)
                val container = root.findViewById<FrameLayout>(R.id.widgetContainer)
                val factory = checkNotNull(registry.xmlFactory(widgetIds[viewType - TYPE_WIDGET_BASE]))
                val widgetHolder = factory.create(container)
                container.addView(widgetHolder.itemView)
                WidgetViewHolder(root, widgetHolder)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val message = getItem(position)) {
            is TextMessage -> (holder as TextViewHolder).bind(message)
            is WidgetMessage -> when (holder) {
                is WidgetViewHolder -> holder.widgetHolder.bind(message)
                is UnknownWidgetViewHolder -> holder.bind(message)
            }
        }
    }

    private class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.findViewById(R.id.text)
        fun bind(message: TextMessage) {
            text.text = message.text
        }
    }

    /** Обёртка над вьюхолдером фичи: RecyclerView-часть у ядра, содержимое — у фичи. */
    private class WidgetViewHolder(
        root: View,
        val widgetHolder: ChatWidgetViewHolder,
    ) : RecyclerView.ViewHolder(root)

    private class UnknownWidgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.findViewById(R.id.text)
        fun bind(message: WidgetMessage) {
            text.text = "Виджет «${message.widgetId}» не поддерживается этой версией приложения"
        }
    }

    private object Diff : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage) = oldItem == newItem
    }

    private companion object {
        const val TYPE_TEXT_IN = 0
        const val TYPE_TEXT_OUT = 1
        const val TYPE_WIDGET_UNKNOWN = 2
        const val TYPE_WIDGET_BASE = 3
    }
}
