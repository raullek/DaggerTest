package az.less.core.deeplink.impl.handler

import az.less.core.deeplink.api.CurrentActivityProvider
import az.less.core.deeplink.api.DeeplinkHandlerBuilder
import az.less.core.deeplink.api.DeeplinkStepFactory
import az.less.core.deeplink.api.DeeplinkStepsRunner
import az.less.core.deeplink.impl.di.DeeplinkScope
import az.less.core.deeplink.impl.presentation.CurrentActivityProviderImpl
import javax.inject.Inject

/**
 * Фабрика для фич: отдаёт билдер с уже встроенным раннером и провайдер Activity —
 * фича собирает обработчик, не зная внутренностей.
 */
@DeeplinkScope
class DeeplinkStepFactoryImpl @Inject constructor(
    private val runner: DeeplinkStepsRunner,
    private val activityProvider: CurrentActivityProviderImpl,
) : DeeplinkStepFactory {

    override fun handlerBuilder(): DeeplinkHandlerBuilder = DeeplinkHandlerBuilder(runner)

    override fun currentActivityProvider(): CurrentActivityProvider = activityProvider
}
