package az.less.core.deeplink.api.step

import az.less.core.deeplink.api.DeeplinkStep
import az.less.core.deeplink.api.DeeplinkUri
import az.less.core.deeplink.api.HandlingResult

/**
 * Декоратор: выполняет [delegate] только если [predicate] истинен для Uri,
 * иначе пропускает шаг (Success).
 */
class PredicateDeeplinkStep(
    private val delegate: DeeplinkStep,
    private val predicate: (DeeplinkUri) -> Boolean,
) : DeeplinkStep {

    override val isEnabled: Boolean get() = delegate.isEnabled

    override val canHandleFailedResultByMyself: Boolean
        get() = delegate.canHandleFailedResultByMyself

    override suspend fun execute(deeplinkUri: DeeplinkUri): HandlingResult =
        if (predicate(deeplinkUri)) delegate.execute(deeplinkUri) else HandlingResult.Success
}
