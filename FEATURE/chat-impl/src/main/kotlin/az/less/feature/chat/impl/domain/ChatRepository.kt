package az.less.feature.chat.impl.domain

import az.less.feature.chat.api.model.ChatMessage
import kotlinx.coroutines.flow.StateFlow

/**
 * Доменный контракт ленты чата. Публичный только технически — его биндит [ChatModule],
 * включаемый в `ChatComponent` app-модуля; в чужих фичах не используется.
 */
interface ChatRepository {

    /** Вся переписка (holder-scoped: переживает пересоздание экранов). */
    val messages: StateFlow<List<ChatMessage>>

    /** Отправить сообщение пользователя и дождаться ответа «сервера». */
    suspend fun send(text: String)
}
