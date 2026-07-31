package az.less.core.deeplink.api

import android.app.Application
import az.less.core.di.FeatureApi

/**
 * Публичное API ядра диплинков.
 *
 * - [attach] — вызывается из Application один раз: ядро начинает следить за
 *   текущей Activity (через ActivityLifecycleCallbacks).
 * - [canHandle] / [handle] — проверка и асинхронная обработка Uri (зовёт
 *   DeeplinkActivity и внутренние переходы).
 * - [stepFactory] — то, чем фичи собирают свои обработчики.
 * - [router] — внутренний запуск диплинка из кода.
 */
interface DeeplinkFeatureApi : FeatureApi {

    fun attach(application: Application)

    fun canHandle(deeplinkUri: DeeplinkUri): Boolean

    suspend fun handle(deeplinkUri: DeeplinkUri): HandlingResult

    fun stepFactory(): DeeplinkStepFactory

    fun currentActivityProvider(): CurrentActivityProvider

    fun router(): DeeplinkRouter
}
