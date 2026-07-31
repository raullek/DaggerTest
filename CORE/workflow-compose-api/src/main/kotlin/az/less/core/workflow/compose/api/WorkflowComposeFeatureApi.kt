package az.less.core.workflow.compose.api

import az.less.core.di.FeatureApi
import az.less.core.workflow.compose.api.engine.BduiStateMachine
import az.less.core.workflow.compose.api.format.FormatterRegistry
import az.less.core.workflow.compose.api.navigation.BduiLauncher
import az.less.core.workflow.compose.api.strategy.StrategyFactory
import az.less.core.workflow.compose.api.widget.Reflector

/**
 * Публичное API BDUI-движка на Compose. Граф собирается лениво через FeatureHolder.
 *
 * - [launcher] — запустить флоу из любой фичи (поднимает Compose-хост);
 * - [newStateMachine] — свежий движок на один запуск флоу;
 * - [reflector] — резолвер рендера (виджеты/поля/контроллеры), сшитый из реестров ядра и фич;
 * - [formatters] — реестр форматтеров server↔ui;
 * - [strategyFactory] — фабрика применителей межвиджетных стратегий.
 */
interface WorkflowComposeFeatureApi : FeatureApi {
    fun launcher(): BduiLauncher
    fun newStateMachine(): BduiStateMachine
    fun reflector(): Reflector
    fun formatters(): FormatterRegistry
    fun strategyFactory(): StrategyFactory
}
