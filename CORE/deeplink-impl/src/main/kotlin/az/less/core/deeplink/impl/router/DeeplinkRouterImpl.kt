package az.less.core.deeplink.impl.router

import android.net.Uri
import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.deeplink.api.DeeplinkRouter
import az.less.core.deeplink.api.DeeplinkUri
import az.less.core.deeplink.impl.di.DeeplinkScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * Внутренний роутер: запускает обработку на собственном scope ядра, поэтому
 * обработка переживает завершение Activity-инициатора (диспетчерская
 * [az.less.core.deeplink.impl.view.DeeplinkActivity] сразу закрывается).
 *
 * Зависит от API через [Provider], чтобы разорвать цикл facade ↔ router.
 */
@DeeplinkScope
class DeeplinkRouterImpl @Inject constructor(
    private val scope: CoroutineScope,
    private val apiProvider: Provider<DeeplinkFeatureApi>,
) : DeeplinkRouter {

    override fun open(uri: Uri) = open(DeeplinkUri.internal(uri))

    override fun open(deeplinkUri: DeeplinkUri) {
        scope.launch { apiProvider.get().handle(deeplinkUri) }
    }
}
