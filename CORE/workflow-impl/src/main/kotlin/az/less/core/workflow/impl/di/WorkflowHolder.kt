package az.less.core.workflow.impl.di

import az.less.core.di.BaseFeatureHolder
import az.less.core.di.FeatureContainer
import az.less.core.workflow.api.WorkflowFeatureApi
import az.less.core.workflow.api.widget.WidgetViewHolderFactory

/**
 * Холдер ядра workflow: лениво строит [WorkflowComponent], передавая ему только вклады виджетов от
 * фич ([az.less.core.workflow.api.widget.FeatureWidgets]); внутренние реестры движка компонент
 * строит сам. Аналог `DeeplinkCoreHolder`.
 */
internal class WorkflowHolder(
    container: FeatureContainer,
    private val featureWidgets: Map<String, @JvmSuppressWildcards WidgetViewHolderFactory>,
) : BaseFeatureHolder<WorkflowFeatureApi>(container) {

    override fun buildFeature(): WorkflowFeatureApi =
        DaggerWorkflowComponent.factory()
            .create(featureWidgets)
}
