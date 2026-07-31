package az.less.core.workflow.api

import az.less.core.di.FeatureApi
import az.less.core.workflow.api.engine.WorkflowStateMachine
import az.less.core.workflow.api.navigation.WorkflowLauncher
import az.less.core.workflow.api.widget.WidgetViewHolderFactory

/**
 * Публичное API ядра server-driven UI.
 *
 * - [launcher] — запустить флоу из любой фичи (поднимает RecyclerView-хост);
 * - [newStateMachine] — свежий движок на один запуск флоу;
 * - [widgetViewHolderFactories] — РЕЕСТР вьюхолдеров виджетов (`type -> фабрика`), собранный
 *   мультибиндингами из ядра и фич; хост-адаптер резолвит по нему `widget.type`.
 *
 * Граф собирается лениво через FeatureHolder (как и остальные ядра проекта).
 */
interface WorkflowFeatureApi : FeatureApi {

    fun launcher(): WorkflowLauncher

    fun newStateMachine(): WorkflowStateMachine

    fun widgetViewHolderFactories(): Map<String, @JvmSuppressWildcards WidgetViewHolderFactory>
}
