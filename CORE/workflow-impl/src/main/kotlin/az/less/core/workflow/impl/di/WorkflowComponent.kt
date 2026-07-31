package az.less.core.workflow.impl.di

import az.less.core.di.PerFeature
import az.less.core.workflow.api.WorkflowFeatureApi
import az.less.core.workflow.api.widget.FeatureWidgets
import az.less.core.workflow.api.widget.WidgetViewHolderFactory
import dagger.BindsInstance
import dagger.Component

/**
 * Граф ядра workflow. Лист графа (фейковый сервер не требует сети). Все внутренние реестры движка
 * (рендереры полей / форматтеры / валидаторы / ядровые виджеты) строятся ВНУТРИ компонента
 * соответствующими модулями. Извне принимает только [FeatureWidgets] — вклады виджетов от фич,
 * агрегированные Dagger'ом в AppComponent (единственное место, видящее все фичи), которые
 * [WorkflowWidgetsModule] сливает с ядровыми. Так корень не тянет внутренности движка.
 *
 * Реализует [WorkflowFeatureApi], поэтому сам компонент и есть публичное API ядра.
 */
@PerFeature
@Component(
    modules = [
        WorkflowModule::class,
        WorkflowWidgetsModule::class,
        WorkflowFieldsModule::class,
        WorkflowFormattersModule::class,
        WorkflowValidatorsModule::class,
    ],
)
internal interface WorkflowComponent : WorkflowFeatureApi {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance @FeatureWidgets featureWidgets: Map<String, @JvmSuppressWildcards WidgetViewHolderFactory>,
        ): WorkflowComponent
    }
}
