package az.less.core.workflow.compose.api.widget

import javax.inject.Qualifier

/**
 * Шов расширения реестра виджетов BDUI-Compose из фич. Помечает `@IntoSet WidgetRegistry`-вклады,
 * приходящие НЕ из ядра, а из фич, и агрегируемые в `AppComponent`. Собранный `@FeatureWidgets Set`
 * прокидывается в `WorkflowComposeComponent` через `@BindsInstance`, где сливается с ядровым
 * `@CoreWidgets`-набором в итоговый `Set<WidgetRegistry>` для рефлектора.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureWidgets
