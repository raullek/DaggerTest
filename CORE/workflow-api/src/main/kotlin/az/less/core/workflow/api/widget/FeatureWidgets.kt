package az.less.core.workflow.api.widget

import javax.inject.Qualifier

/**
 * Шов расширения реестра виджетов из фич. Помечает `@IntoMap @WidgetTypeKey`-вклады, которые
 * приходят НЕ из ядра, а из фич, и агрегируются в `AppComponent` (единственное место, видящее
 * вклады всех фич). Собранная `@FeatureWidgets Map` прокидывается в `WorkflowComponent` через
 * `@BindsInstance`, где сливается с ядровой [Core... ] картой в итоговый реестр.
 *
 * Так корневой компонент не тянет внутренние реестры ядра — только вклады фич (одна строка на фичу).
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureWidgets
