package az.less.core.deeplink.api

/**
 * Результат шага/обработчика. Sealed — чтобы у провала был повод (для логов
 * и [FailedResultHandler]).
 */
sealed interface HandlingResult {

    data object Success : HandlingResult

    data class Failed(val reason: String) : HandlingResult

    val isSuccess: Boolean get() = this is Success
    val isFailed: Boolean get() = this is Failed
}
