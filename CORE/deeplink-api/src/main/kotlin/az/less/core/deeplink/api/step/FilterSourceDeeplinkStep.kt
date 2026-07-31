package az.less.core.deeplink.api.step

import az.less.core.deeplink.api.DeeplinkStep
import az.less.core.deeplink.api.DeeplinkUri
import az.less.core.deeplink.api.HandlingResult

/** Источник диплинка, для которого шаг актуален. */
enum class DeeplinkSource { EXTERNAL, INTERNAL, ANY }

/**
 * Декоратор: выполняет [delegate] только для нужного источника (внешний/внутренний),
 * иначе пропускает (Success).
 */
class FilterSourceDeeplinkStep(
    private val source: DeeplinkSource,
    private val delegate: DeeplinkStep,
) : DeeplinkStep {

    override val isEnabled: Boolean get() = delegate.isEnabled

    override val canHandleFailedResultByMyself: Boolean
        get() = delegate.canHandleFailedResultByMyself

    override suspend fun execute(deeplinkUri: DeeplinkUri): HandlingResult {
        val matches = when (source) {
            DeeplinkSource.ANY -> true
            DeeplinkSource.EXTERNAL -> deeplinkUri.isExternal
            DeeplinkSource.INTERNAL -> !deeplinkUri.isExternal
        }
        return if (matches) delegate.execute(deeplinkUri) else HandlingResult.Success
    }
}
