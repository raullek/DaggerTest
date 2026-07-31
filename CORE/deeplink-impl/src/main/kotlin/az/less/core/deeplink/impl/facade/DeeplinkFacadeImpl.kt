package az.less.core.deeplink.impl.facade

import android.app.Application
import android.util.Log
import az.less.core.deeplink.api.CurrentActivityProvider
import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.deeplink.api.DeeplinkRouter
import az.less.core.deeplink.api.DeeplinkStepFactory
import az.less.core.deeplink.api.DeeplinkUri
import az.less.core.deeplink.api.HandlingResult
import az.less.core.deeplink.impl.di.DeeplinkScope
import az.less.core.deeplink.impl.presentation.CurrentActivityProviderImpl
import az.less.core.deeplink.impl.storage.DeeplinkHandlerStorage
import javax.inject.Inject
import javax.inject.Provider

/**
 * Точка входа ядра:
 * валидирует Uri → ищет обработчик в [DeeplinkHandlerStorage] → запускает его.
 *
 * Реализует [DeeplinkFeatureApi] — именно её отдаёт холдер наружу.
 */
@DeeplinkScope
class DeeplinkFacadeImpl @Inject constructor(
    private val storage: DeeplinkHandlerStorage,
    private val stepFactory: DeeplinkStepFactory,
    private val activityProvider: CurrentActivityProviderImpl,
    private val routerProvider: Provider<DeeplinkRouter>,
) : DeeplinkFeatureApi {

    override fun attach(application: Application) {
        activityProvider.registerWith(application)
    }

    override fun canHandle(deeplinkUri: DeeplinkUri): Boolean = storage.hasHandler(deeplinkUri)

    override suspend fun handle(deeplinkUri: DeeplinkUri): HandlingResult {
        if (!isValid(deeplinkUri)) {
            Log.w(TAG, "Invalid deeplink: ${deeplinkUri.uri}")
            return HandlingResult.Failed("Invalid deeplink: ${deeplinkUri.uri}")
        }
        val handler = storage.findHandler(deeplinkUri)
        if (handler == null) {
            Log.w(TAG, "No handler for: ${deeplinkUri.uri}")
            return HandlingResult.Failed("No handler for ${deeplinkUri.uri}")
        }
        if (!handler.isEnabled) {
            return HandlingResult.Failed("Handler disabled for ${deeplinkUri.uri}")
        }
        Log.d(TAG, "Handling ${deeplinkUri.uri}")
        return handler.handle(deeplinkUri)
    }

    override fun stepFactory(): DeeplinkStepFactory = stepFactory

    override fun currentActivityProvider(): CurrentActivityProvider = activityProvider

    override fun router(): DeeplinkRouter = routerProvider.get()

    /** Минимальная валидация: схема — одна из наших. */
    private fun isValid(deeplinkUri: DeeplinkUri): Boolean {
        val scheme = deeplinkUri.uri.scheme?.lowercase()
        return scheme != null && deeplinkUri.uri.authority != null
    }

    private companion object {
        const val TAG = "Deeplink"
    }
}
