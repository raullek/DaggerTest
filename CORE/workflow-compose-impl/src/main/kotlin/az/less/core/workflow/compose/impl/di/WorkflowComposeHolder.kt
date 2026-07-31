package az.less.core.workflow.compose.impl.di

import az.less.core.di.BaseFeatureHolder
import az.less.core.di.FeatureContainer
import az.less.core.workflow.compose.api.WorkflowComposeFeatureApi
import az.less.core.workflow.compose.api.widget.WidgetRegistry

/**
 * Холдер ядра BDUI-Compose: лениво строит [WorkflowComposeComponent], передавая ему только вклады
 * виджетов от фич ([az.less.core.workflow.compose.api.widget.FeatureWidgets]); внутренние реестры
 * движка компонент строит сам.
 */
internal class WorkflowComposeHolder(
    container: FeatureContainer,
    private val featureWidgets: Set<@JvmSuppressWildcards WidgetRegistry>,
) : BaseFeatureHolder<WorkflowComposeFeatureApi>(container) {

    override fun buildFeature(): WorkflowComposeFeatureApi =
        DaggerWorkflowComposeComponent.factory()
            .create(featureWidgets)
}
