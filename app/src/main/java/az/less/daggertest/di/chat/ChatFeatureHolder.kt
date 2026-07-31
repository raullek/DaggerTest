package az.less.daggertest.di.chat

import az.less.core.di.BaseFeatureHolder
import az.less.core.di.FeatureContainer
import az.less.feature.chat.api.ChatFeatureApi

/**
 * Холдер фичи чата. Живёт в app-модуле рядом с компонентом: обычный ленивый холдер,
 * агрегаты через него НЕ проезжают —
 * они собираются внутри [ChatComponent] его же модулями. Чат — лист графа, зависимостей нет.
 */
internal class ChatFeatureHolder(
    container: FeatureContainer,
) : BaseFeatureHolder<ChatFeatureApi>(container) {

    override fun buildFeature(): ChatFeatureApi =
        DaggerChatComponent.factory().create()
}
