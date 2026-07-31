package az.less.core.deeplink.api

/**
 * Единица работы при обработке диплинка — звено цепочки.
 *
 * `execute` — `suspend`. Шаги выполняются строго последовательно и цепочка
 * обрывается на первом [HandlingResult.Failed] (см. DeeplinkStepsHandler в :impl).
 *
 * - [isEnabled] — выключенный шаг пропускается (фиче-тоглы).
 * - [canHandleFailedResultByMyself] — если шаг сам показывает ошибку
 *   пользователю, общий [FailedResultHandler] для него не вызывается.
 */
interface DeeplinkStep {

    val isEnabled: Boolean get() = true

    val canHandleFailedResultByMyself: Boolean get() = false

    suspend fun execute(deeplinkUri: DeeplinkUri): HandlingResult
}
