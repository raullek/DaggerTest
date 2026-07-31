package az.less.feature.chat.api.widget

import android.view.View
import az.less.feature.chat.api.model.WidgetMessage

/**
 * Вьюхолдер одного виджета в XML-ленте чата. Фича-поставщик сама владеет своей вёрсткой
 * и логикой байнда — ядро чата видит только этот контракт
 * (зеркало `WidgetViewHolder` ядра workflow).
 */
abstract class ChatWidgetViewHolder(val itemView: View) {

    /** Привязать данные виджет-сообщения (payload — сырые строки от сервера). */
    abstract fun bind(message: WidgetMessage)
}
