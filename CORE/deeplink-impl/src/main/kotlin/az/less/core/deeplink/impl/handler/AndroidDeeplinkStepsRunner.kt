package az.less.core.deeplink.impl.handler

import android.util.Log
import az.less.core.deeplink.api.DeeplinkStep
import az.less.core.deeplink.api.DeeplinkStepsRunner
import az.less.core.deeplink.api.DeeplinkUri
import az.less.core.deeplink.api.FailedResultHandler
import az.less.core.deeplink.api.HandlingResult
import az.less.core.deeplink.impl.di.DeeplinkScope
import javax.inject.Inject

/**
 * Прогон шагов — на корутинах:
 *
 * - шаги идут строго последовательно (просто `for` по списку); каждый `execute` —
 *   suspend, поэтому шаг, открывший экран, успевает отработать прежде чем
 *   стартует следующий;
 * - выключенные шаги пропускаются;
 * - цепочка обрывается на первом [HandlingResult.Failed];
 * - если упавший шаг не умеет показать ошибку сам — зовём общий [FailedResultHandler].
 */
@DeeplinkScope
class AndroidDeeplinkStepsRunner @Inject constructor() : DeeplinkStepsRunner {

    override suspend fun run(
        steps: List<DeeplinkStep>,
        failedResultHandler: FailedResultHandler,
        deeplinkUri: DeeplinkUri,
    ): HandlingResult {
        for (step in steps) {
            if (!step.isEnabled) continue

            val result = runCatching { step.execute(deeplinkUri) }
                .getOrElse { error ->
                    Log.e(TAG, "Step ${step.javaClass.simpleName} threw", error)
                    HandlingResult.Failed("Step ${step.javaClass.simpleName} crashed: ${error.message}")
                }

            if (result is HandlingResult.Failed) {
                if (!step.canHandleFailedResultByMyself) {
                    failedResultHandler.handleFailedResult(deeplinkUri, result)
                }
                return result
            }
        }
        return HandlingResult.Success
    }

    private companion object {
        const val TAG = "Deeplink"
    }
}
