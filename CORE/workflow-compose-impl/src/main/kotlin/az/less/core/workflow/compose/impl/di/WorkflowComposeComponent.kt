package az.less.core.workflow.compose.impl.di

import az.less.core.di.PerFeature
import az.less.core.workflow.compose.api.WorkflowComposeFeatureApi
import az.less.core.workflow.compose.api.widget.FeatureWidgets
import az.less.core.workflow.compose.api.widget.WidgetRegistry
import dagger.BindsInstance
import dagger.Component

/**
 * Граф ядра BDUI-Compose. Лист графа (фейковый сервер не требует сети). Внутренние реестры движка
 * (валидаторы для маппера, ядровые реестры виджетов) строятся ВНУТРИ компонента. Извне принимает
 * только [FeatureWidgets] — вклады виджетов от фич, агрегированные в AppComponent, которые
 * [WorkflowComposeWidgetsModule] сливает с ядровыми. Реализует [WorkflowComposeFeatureApi].
 */
@PerFeature
@Component(
    modules = [
        WorkflowComposeModule::class,
        WorkflowComposeValidatorsModule::class,
        WorkflowComposeWidgetsModule::class,
    ],
)
internal interface WorkflowComposeComponent : WorkflowComposeFeatureApi {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance @FeatureWidgets featureWidgets: Set<@JvmSuppressWildcards WidgetRegistry>,
        ): WorkflowComposeComponent
    }
}
