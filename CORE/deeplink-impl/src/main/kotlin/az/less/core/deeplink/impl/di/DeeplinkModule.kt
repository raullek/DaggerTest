package az.less.core.deeplink.impl.di

import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.deeplink.api.DeeplinkHandlerEntry
import az.less.core.deeplink.api.DeeplinkRouter
import az.less.core.deeplink.api.DeeplinkStepFactory
import az.less.core.deeplink.api.DeeplinkStepsRunner
import az.less.core.deeplink.api.DeeplinkUriMatcher
import az.less.core.deeplink.impl.facade.DeeplinkFacadeImpl
import az.less.core.deeplink.impl.handler.AndroidDeeplinkStepsRunner
import az.less.core.deeplink.impl.handler.DeeplinkStepFactoryImpl
import az.less.core.deeplink.impl.router.DeeplinkRouterImpl
import az.less.core.deeplink.impl.storage.DeeplinkHandlerStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Граф ядра диплинков. Связывает реализации с контрактами :api и собирает
 * [DeeplinkHandlerStorage] из мультибиндингов, которые приходят снаружи
 * (см. [DeeplinkComponent.Factory] — `Set<DeeplinkHandlerEntry>`/`Set<DeeplinkUriMatcher>`).
 */
@Module
internal interface DeeplinkModule {

    @Binds
    @DeeplinkScope
    fun bindApi(impl: DeeplinkFacadeImpl): DeeplinkFeatureApi

    @Binds
    @DeeplinkScope
    fun bindRouter(impl: DeeplinkRouterImpl): DeeplinkRouter

    @Binds
    @DeeplinkScope
    fun bindStepsRunner(impl: AndroidDeeplinkStepsRunner): DeeplinkStepsRunner

    @Binds
    @DeeplinkScope
    fun bindStepFactory(impl: DeeplinkStepFactoryImpl): DeeplinkStepFactory

    companion object {

        @Provides
        @DeeplinkScope
        fun provideScope(): CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

        @Provides
        @DeeplinkScope
        fun provideStorage(
            entries: Set<@JvmSuppressWildcards DeeplinkHandlerEntry>,
            matchers: Set<@JvmSuppressWildcards DeeplinkUriMatcher>,
        ): DeeplinkHandlerStorage = DeeplinkHandlerStorage(entries, matchers)
    }
}
