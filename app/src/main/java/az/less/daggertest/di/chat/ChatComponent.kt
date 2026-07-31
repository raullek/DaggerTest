package az.less.daggertest.di.chat

import az.less.core.di.PerFeature
import az.less.feature.chat.impl.di.ChatInternalApi
import az.less.feature.chat.impl.di.ChatModule
import dagger.Component

/**
 * Граф фичи чата. Объявлен в APP-модуле — схема для фич-агрегаторов: компонент
 * собирается там, где видны все impl, поэтому Compound-модуль с вкладами фич включается
 * ПРЯМО в `modules`. Мультибиндинги (`@ChatWidgets Map`, `Set<ChatQuickAction>`)
 * материализуются внутри ЭТОГО графа — лениво, при первом обращении к фиче,
 * а не в корневом AppComponent.
 *
 * Внутренние биндинги ([ChatModule]) и контракт [ChatInternalApi] остаются в impl.
 */
@PerFeature
@Component(
    modules = [
        ChatModule::class,
        CompoundChatWidgetsModule::class,
    ],
)
interface ChatComponent : ChatInternalApi {

    @Component.Factory
    interface Factory {
        fun create(): ChatComponent
    }
}
