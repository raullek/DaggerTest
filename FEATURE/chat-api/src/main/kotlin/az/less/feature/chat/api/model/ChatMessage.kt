package az.less.feature.chat.api.model

/**
 * Доменная модель сообщения чата. Модель ПУБЛИЧНАЯ (api-слой), потому что виджет-сообщение
 * попадает в контракты рендеров ([az.less.feature.chat.api.widget.ChatWidgetViewHolderFactory],
 * [az.less.feature.chat.api.widget.ChatWidgetComposer]), которые реализуют другие фичи.
 */
sealed interface ChatMessage {
    val id: Long
}

/** Обычный текстовый пузырь. [incoming] = от ассистента (слева), иначе — от пользователя (справа). */
data class TextMessage(
    override val id: Long,
    val incoming: Boolean,
    val text: String,
) : ChatMessage

/**
 * Server-driven виджет в ленте: сервер присылает только строковый [widgetId] и сырой [payload],
 * а КТО и КАК его отрисует — решает клиентский реестр `Map<String, …>` из Dagger-мультибиндингов.
 * Неизвестный id — не ошибка: рендер обязан показать заглушку.
 */
data class WidgetMessage(
    override val id: Long,
    val widgetId: String,
    val payload: Map<String, String> = emptyMap(),
) : ChatMessage
