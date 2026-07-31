package az.less.core.deeplink.api

/**
 * Обработчик одного семейства диплинков — выполняет свой список [DeeplinkStep].
 *
 * Создаётся лениво через [DeeplinkHandlerEntry.handlerProvider] только когда Uri
 * реально совпал, поэтому граф фичи не строится на старте приложения.
 */
interface DeeplinkHandler {

    /** Включён ли обработчик целиком (фиче-тогл всей фичи). */
    val isEnabled: Boolean get() = true

    suspend fun handle(deeplinkUri: DeeplinkUri): HandlingResult
}
