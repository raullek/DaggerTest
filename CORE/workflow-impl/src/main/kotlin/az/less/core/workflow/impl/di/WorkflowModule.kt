package az.less.core.workflow.impl.di

import az.less.core.di.PerFeature
import az.less.core.workflow.api.check.WorkflowResultValidator
import az.less.core.workflow.api.engine.WorkflowLogger
import az.less.core.workflow.api.engine.WorkflowRepository
import az.less.core.workflow.api.engine.WorkflowStateMachine
import az.less.core.workflow.api.navigation.WorkflowLauncher
import az.less.core.workflow.impl.check.WorkflowResultValidatorImpl
import az.less.core.workflow.impl.data.FakeWorkflowApi
import az.less.core.workflow.impl.data.WorkflowApi
import az.less.core.workflow.impl.data.WorkflowRepositoryImpl
import az.less.core.workflow.impl.engine.WorkflowStateMachineImpl
import az.less.core.workflow.impl.logging.AndroidWorkflowLogger
import az.less.core.workflow.impl.navigation.WorkflowLauncherImpl
import dagger.Binds
import dagger.Module

/**
 * Граф ядра workflow (движок/репозиторий/лаунчер). Реестры (вьюхолдеры/поля/форматтеры/валидаторы)
 * собираются в AppComponent и приходят сюда через `@BindsInstance` (см. [WorkflowComponent]).
 *
 * Скоупы: движок и валидатор результата — БЕЗ скоупа (свежие на каждый `newStateMachine()`),
 * лаунчер — `@PerFeature`.
 */
@Module
internal interface WorkflowModule {

    @Binds
    @PerFeature
    fun bindLauncher(impl: WorkflowLauncherImpl): WorkflowLauncher

    @Binds
    fun bindWorkflowApi(impl: FakeWorkflowApi): WorkflowApi

    @Binds
    fun bindRepository(impl: WorkflowRepositoryImpl): WorkflowRepository

    @Binds
    fun bindStateMachine(impl: WorkflowStateMachineImpl): WorkflowStateMachine

    @Binds
    fun bindResultValidator(impl: WorkflowResultValidatorImpl): WorkflowResultValidator

    // Логгер экшенов SDUI. Подмени биндинг, чтобы отправлять в аналитику вместо Logcat.
    @Binds
    fun bindLogger(impl: AndroidWorkflowLogger): WorkflowLogger
}
