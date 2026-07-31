package az.less.core.workflow.compose.impl.di

import az.less.core.di.PerFeature
import az.less.core.workflow.compose.api.check.WorkflowResultValidator
import az.less.core.workflow.compose.api.engine.BduiStateMachine
import az.less.core.workflow.compose.api.engine.WorkflowLogger
import az.less.core.workflow.compose.api.engine.WorkflowRepository
import az.less.core.workflow.compose.api.format.FormatterRegistry
import az.less.core.workflow.compose.api.navigation.BduiLauncher
import az.less.core.workflow.compose.api.strategy.StrategyFactory
import az.less.core.workflow.compose.api.widget.Reflector
import az.less.core.workflow.compose.api.widget.WidgetRegistry
import az.less.core.workflow.compose.impl.check.WorkflowResultValidatorImpl
import az.less.core.workflow.compose.impl.data.FakeBduiApi
import az.less.core.workflow.compose.impl.data.WorkflowApi
import az.less.core.workflow.compose.impl.data.WorkflowRepositoryImpl
import az.less.core.workflow.compose.impl.engine.BduiStateMachineImpl
import az.less.core.workflow.compose.impl.format.DefaultFormatterRegistry
import az.less.core.workflow.compose.impl.logging.AndroidWorkflowLogger
import az.less.core.workflow.compose.impl.navigation.BduiLauncherImpl
import az.less.core.workflow.compose.impl.reflector.ComplexReflector
import az.less.core.workflow.compose.impl.strategy.DefaultStrategyFactory
import az.less.core.workflow.compose.impl.ui.FieldRenderers
import az.less.core.workflow.compose.impl.ui.WidgetRenderers
import dagger.Binds
import dagger.Module
import dagger.Provides

/**
 * Граф ядра BDUI-Compose. Реестры (валидаторы/виджеты) приходят через `@BindsInstance` из AppComponent
 * (см. [WorkflowComposeComponent]).
 *
 * Скоупы: движок/репозиторий/api/валидатор результата — БЕЗ скоупа (свежие на каждый
 * `newStateMachine()`; FakeBduiApi так копит поля в рамках одного прогона); лаунчер/форматтеры/
 * стратегии/reflector — `@PerFeature`.
 */
@Module
internal interface WorkflowComposeModule {

    @Binds
    @PerFeature
    fun bindLauncher(impl: BduiLauncherImpl): BduiLauncher

    @Binds
    fun bindApi(impl: FakeBduiApi): WorkflowApi

    @Binds
    fun bindRepository(impl: WorkflowRepositoryImpl): WorkflowRepository

    @Binds
    fun bindStateMachine(impl: BduiStateMachineImpl): BduiStateMachine

    @Binds
    fun bindResultValidator(impl: WorkflowResultValidatorImpl): WorkflowResultValidator

    @Binds
    @PerFeature
    fun bindFormatters(impl: DefaultFormatterRegistry): FormatterRegistry

    @Binds
    @PerFeature
    fun bindStrategyFactory(impl: DefaultStrategyFactory): StrategyFactory

    @Binds
    fun bindLogger(impl: AndroidWorkflowLogger): WorkflowLogger

    companion object {
        /**
         * Reflector сшивает реестры виджетов (ядро + фичи) с дефолтным FIELDSET и картой рендереров
         * полей.
         */
        @Provides
        @PerFeature
        fun provideReflector(
            widgetRegistries: Set<@JvmSuppressWildcards WidgetRegistry>,
        ): Reflector = ComplexReflector(
            widgetRegistries = widgetRegistries.toList(),
            defaultWidgetRenderer = WidgetRenderers.FieldSet,
            fieldRenderers = FieldRenderers.byType,
            defaultFieldRenderer = FieldRenderers.default,
        )
    }
}
