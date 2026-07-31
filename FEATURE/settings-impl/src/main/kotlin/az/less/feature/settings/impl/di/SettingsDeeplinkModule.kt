package az.less.feature.settings.impl.di

import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.deeplink.api.DeeplinkHandlerEntry
import az.less.core.deeplink.api.DeeplinkUri
import az.less.core.deeplink.api.Deeplinks
import az.less.core.deeplink.api.HandlingResult
import az.less.core.deeplink.api.step.ConditionDeeplinkStep
import az.less.core.deeplink.api.step.LaunchFeatureDeeplinkStep
import az.less.core.di.api
import az.less.feature.settings.api.SettingsFeatureApi
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

/**
 * Вклад фичи settings: обработчик `app-app://az.less.daggertest/settings`.
 *
 * Демонстрирует цепочку из двух шагов: сперва [ConditionDeeplinkStep] (здесь —
 * проверка query-параметра `?ok=true`), затем
 * навигация. Если условие не выполнено — цепочка обрывается на первом шаге и до
 * запуска экрана дело не доходит.
 */
@Module
object SettingsDeeplinkModule {

    @Provides
    @IntoSet
    fun provideSettingsDeeplinkEntry(): DeeplinkHandlerEntry =
        DeeplinkHandlerEntry(uri = Deeplinks.feature(PATH)) {
            val factory = api<DeeplinkFeatureApi>().stepFactory()
            factory.handlerBuilder()
                .addStep(RequireOkParamStep())
                .addStep(
                    LaunchFeatureDeeplinkStep(factory.currentActivityProvider()) { fragmentManager ->
                        api<SettingsFeatureApi>().launcher().launch(fragmentManager)
                    },
                )
                .build()
        }

    private const val PATH = "settings"

    /** Пускает дальше только если в Uri есть `?ok=true`. Иначе — Failed (демо условия). */
    private class RequireOkParamStep : ConditionDeeplinkStep() {
        override suspend fun checkCondition(deeplinkUri: DeeplinkUri): Boolean =
            deeplinkUri.uri.getQueryParameter("ok") == "true"

        override suspend fun onConditionFailed(deeplinkUri: DeeplinkUri): HandlingResult =
            HandlingResult.Failed("settings deeplink requires ?ok=true")
    }
}
