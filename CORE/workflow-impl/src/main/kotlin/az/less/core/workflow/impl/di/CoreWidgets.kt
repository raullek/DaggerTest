package az.less.core.workflow.impl.di

import javax.inject.Qualifier

/**
 * Внутренний квалификатор: ядровые виджеты движка (`@IntoMap @CoreWidgets`), собираемые целиком
 * внутри [WorkflowComponent]. Сливаются с [az.less.core.workflow.api.widget.FeatureWidgets]-вкладами
 * фич в итоговый `Map<String, WidgetViewHolderFactory>` (см. [WorkflowWidgetsModule]).
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class CoreWidgets
