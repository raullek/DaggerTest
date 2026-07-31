package az.less.core.workflow.compose.impl.di

import az.less.core.workflow.compose.api.widget.FeatureWidgets
import az.less.core.workflow.compose.api.widget.WidgetRegistry
import az.less.core.workflow.compose.impl.ui.CoreWidgetRegistry
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

/**
 * Реестр виджет-рендереров ядра BDUI-Compose. Живёт ВНУТРИ [WorkflowComposeComponent]: ядровой
 * [CoreWidgetRegistry] помечен [CoreWidgets], вклады фич приходят как [FeatureWidgets] через
 * `@BindsInstance`. [widgetRegistries] сливает их в итоговый `Set<WidgetRegistry>`, который
 * ComplexReflector сшивает first-hit-wins. Так корень не тянет внутренности движка. Фича добавляет
 * свой `@IntoSet @FeatureWidgets WidgetRegistry`, ядро не трогается.
 */
@Module
interface WorkflowComposeWidgetsModule {

    companion object {
        @Provides
        @IntoSet
        @CoreWidgets
        fun coreWidgets(): WidgetRegistry = CoreWidgetRegistry

        /** Итоговый набор: ядровые реестры + вклады фич. */
        @Provides
        fun widgetRegistries(
            @CoreWidgets core: Set<@JvmSuppressWildcards WidgetRegistry>,
            @FeatureWidgets feature: Set<@JvmSuppressWildcards WidgetRegistry>,
        ): Set<@JvmSuppressWildcards WidgetRegistry> = core + feature
    }
}
