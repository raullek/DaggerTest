package az.less.core.workflow.compose.impl.di

import javax.inject.Qualifier

/**
 * Внутренний квалификатор: ядровые реестры виджетов BDUI-Compose (`@IntoSet @CoreWidgets`),
 * собираемые целиком внутри [WorkflowComposeComponent]. Сливаются с
 * [az.less.core.workflow.compose.api.widget.FeatureWidgets]-вкладами фич в итоговый
 * `Set<WidgetRegistry>` для рефлектора (см. [WorkflowComposeWidgetsModule]).
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class CoreWidgets
