package az.less.feature.chat.impl.data

import az.less.feature.chat.api.model.ChatMessage
import az.less.feature.chat.impl.domain.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Состояние переписки. Живёт @PerFeature в графе чата, поэтому лента ОБЩАЯ для обоих
 * рендеров: открыв Compose-экран после XML, увидим ту же историю.
 */
class ChatRepositoryImpl @Inject constructor(
    private val server: FakeChatServer,
) : ChatRepository {

    private val _messages = MutableStateFlow(server.greeting())

    override val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    override suspend fun send(text: String) {
        _messages.update { it + server.outgoing(text) }
        _messages.update { it + server.reply(text) }
    }
}
