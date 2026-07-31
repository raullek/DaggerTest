package az.less.feature.chat.impl.domain

import az.less.feature.chat.api.model.ChatMessage
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Доменный интерактор чата — тонкая обёртка над репозиторием:
 * presentation не ходит в data напрямую.
 */
class ChatInteractor @Inject constructor(
    private val repository: ChatRepository,
) {

    val messages: StateFlow<List<ChatMessage>> get() = repository.messages

    suspend fun send(text: String) {
        if (text.isNotBlank()) repository.send(text.trim())
    }
}
