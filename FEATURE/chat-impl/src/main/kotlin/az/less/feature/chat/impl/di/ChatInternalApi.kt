package az.less.feature.chat.impl.di

import az.less.core.di.api
import az.less.feature.chat.api.ChatFeatureApi
import az.less.feature.chat.impl.domain.ChatInteractor
import az.less.feature.chat.impl.domain.ChatWidgetRegistry

/**
 * Внутреннее API графа чата: то, что нужно СВОИМ экранам, но не должно торчать наружу
 * (паттерн InternalApi extends ExternalApi).
 * Интерфейс публичный, потому что его реализует `ChatComponent` из app-модуля.
 */
interface ChatInternalApi : ChatFeatureApi {
    fun interactor(): ChatInteractor
    fun widgetRegistry(): ChatWidgetRegistry
}

/** Достаём внутреннее API из DI: компонент чата реализует и внешний, и внутренний контракты. */
internal fun chatInternalApi(): ChatInternalApi = api<ChatFeatureApi>() as ChatInternalApi
