package az.less.feature.transfer.api

import az.less.core.di.FeatureApi

/**
 * Фича «Перевод денег» — целиком server-driven: у неё нет своих экранов/репозитория, весь UX
 * приходит с сервера через ядро workflow. Наружу торчит только лаунчер.
 */
interface TransferFeatureApi : FeatureApi {
    fun launcher(): TransferLauncher
}
