package az.less.feature.chat.impl.presentation

import az.less.feature.chat.api.action.ChatQuickAction
import az.less.feature.chat.api.model.ChatMessage
import az.less.feature.chat.impl.domain.ChatInteractor
import az.less.feature.chat.impl.domain.ChatWidgetRegistry
import kotlinx.coroutines.flow.StateFlow

/**
 * Общая вьюмодель обоих рендеров (XML и Compose): лента из интерактора + вклады фич из реестра.
 * Обычный класс без androidx.ViewModel — по конвенции проекта (см. ProfileViewModel);
 * состояние переживает пересоздание экрана, потому что живёт в @PerFeature-репозитории.
 */
internal class ChatViewModel(
    private val interactor: ChatInteractor,
    private val registry: ChatWidgetRegistry,
) {

    val messages: StateFlow<List<ChatMessage>> get() = interactor.messages

    /** Чипы быстрых действий: агрегат Set<ChatQuickAction>, фильтр + сортировка в реестре. */
    fun quickActions(): List<ChatQuickAction> = registry.quickActions()

    suspend fun send(text: String) = interactor.send(text)
}
