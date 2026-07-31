package az.less.core.deeplink.api.step

import az.less.core.deeplink.api.DeeplinkStep
import az.less.core.deeplink.api.DeeplinkUri
import az.less.core.deeplink.api.HandlingResult

/**
 * Шаг-условие: проверяет [checkCondition];
 * при успехе — [onConditionSuccess] (по умолчанию Success), при провале —
 * [onConditionFailed] (по умолчанию Failed). Наследник переопределяет ветки,
 * чтобы, например, показать диалог/экран перед тем как пустить дальше.
 */
abstract class ConditionDeeplinkStep : DeeplinkStep {

    protected abstract suspend fun checkCondition(deeplinkUri: DeeplinkUri): Boolean

    protected open suspend fun onConditionSuccess(deeplinkUri: DeeplinkUri): HandlingResult =
        HandlingResult.Success

    protected open suspend fun onConditionFailed(deeplinkUri: DeeplinkUri): HandlingResult =
        HandlingResult.Failed("Condition not met for ${deeplinkUri.uri}")

    final override suspend fun execute(deeplinkUri: DeeplinkUri): HandlingResult =
        if (checkCondition(deeplinkUri)) onConditionSuccess(deeplinkUri)
        else onConditionFailed(deeplinkUri)
}
