package az.less.feature.chat.impl.data

import az.less.feature.chat.api.model.ChatMessage
import az.less.feature.chat.api.model.TextMessage
import az.less.feature.chat.api.model.WidgetMessage
import az.less.feature.chat.api.widget.ChatWidgetIds
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

/**
 * Фейковый чат-сервер (как fake server у workflow: сети нет, поведение — скрипт).
 * Ключевая идея server-driven: сервер отвечает СТРОКОВЫМИ id виджетов ([ChatWidgetIds]),
 * ничего не зная о клиентских классах — клиент резолвит id через Dagger-карту.
 * Отвечает и заведомо НЕИЗВЕСТНЫМ клиенту id (`GIBDD_FINES`) — проверка фолбэка.
 */
class FakeChatServer @Inject constructor() {

    private val nextId = AtomicLong(1)

    /** Приветствие ассистента при открытии чата. */
    fun greeting(): List<ChatMessage> = listOf(
        incoming(
            "Привет! Я ассистент. Напишите «профиль», «каталог», «перевод», «штраф» " +
                "или «всё» — покажу виджеты. Чипы снизу ведут в фичи напрямую.",
        ),
    )

    /** «Сетевой» ответ сервера на сообщение пользователя (эмулируем задержку). */
    suspend fun reply(userText: String): List<ChatMessage> {
        delay(REPLY_DELAY_MS)
        val text = userText.lowercase()
        return when {
            text.contains("профил") -> listOf(
                incoming("Вот ваш профиль:"),
                widget(ChatWidgetIds.PROFILE_CARD),
            )

            text.contains("каталог") || text.contains("товар") -> listOf(
                incoming("Подобрал товары для вас:"),
                widget(ChatWidgetIds.CATALOG_SHOWCASE, payload = mapOf("limit" to "3")),
            )

            text.contains("перевод") || text.contains("деньг") -> listOf(
                incoming("Могу отправить перевод:"),
                widget(ChatWidgetIds.TRANSFER_ACTION, payload = mapOf("amount" to "1000")),
            )

            // Сервер «умеет» больше клиента: этот id клиенту неизвестен → рендер покажет заглушку.
            text.contains("штраф") -> listOf(
                incoming("Нашёл один штраф:"),
                widget("GIBDD_FINES", payload = mapOf("amount" to "500")),
            )

            text.contains("всё") || text.contains("все") -> listOf(
                incoming("Всё, что я умею:"),
                widget(ChatWidgetIds.PROFILE_CARD),
                widget(ChatWidgetIds.CATALOG_SHOWCASE, payload = mapOf("limit" to "3")),
                widget(ChatWidgetIds.TRANSFER_ACTION, payload = mapOf("amount" to "1000")),
                widget("GIBDD_FINES", payload = mapOf("amount" to "500")),
            )

            else -> listOf(
                incoming("Не понял. Попробуйте «профиль», «каталог», «перевод», «штраф» или «всё»."),
            )
        }
    }

    /** Сообщение пользователя (сервер присваивает id, как настоящий бэкенд). */
    fun outgoing(text: String): ChatMessage = TextMessage(nextId.getAndIncrement(), incoming = false, text = text)

    private fun incoming(text: String): ChatMessage =
        TextMessage(nextId.getAndIncrement(), incoming = true, text = text)

    private fun widget(widgetId: String, payload: Map<String, String> = emptyMap()): ChatMessage =
        WidgetMessage(nextId.getAndIncrement(), widgetId, payload)

    private companion object {
        const val REPLY_DELAY_MS = 600L
    }
}
