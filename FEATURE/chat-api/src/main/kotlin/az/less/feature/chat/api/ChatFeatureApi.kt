package az.less.feature.chat.api

import az.less.core.di.FeatureApi

/**
 * Публичное API фичи «Чат с ассистентом». Наружу торчит только лаунчер — весь UX внутри.
 *
 * Чат — экран-агрегатор: сам он ни одной фичи не знает,
 * а его виджеты и быстрые действия приходят из других фич через Dagger-мультибиндинги
 * (см. контракты в пакете [az.less.feature.chat.api.widget] и [az.less.feature.chat.api.action]).
 */
interface ChatFeatureApi : FeatureApi {
    fun launcher(): ChatLauncher
}
