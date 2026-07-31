package az.less.core.workflow.impl.di

import az.less.core.workflow.api.widget.FeatureWidgets
import az.less.core.workflow.api.widget.ScreenItem
import az.less.core.workflow.api.widget.WidgetTypeKey
import az.less.core.workflow.api.widget.WidgetViewHolderFactory
import az.less.core.workflow.impl.render.BannerViewHolderFactory
import az.less.core.workflow.impl.render.EventViewHolderFactory
import az.less.core.workflow.impl.render.FieldSetViewHolderFactory
import az.less.core.workflow.impl.render.HeaderViewHolderFactory
import az.less.core.workflow.impl.render.StepperViewHolderFactory
import az.less.core.workflow.impl.render.SummaryViewHolderFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

/**
 * Реестр вьюхолдеров виджетов ядра: `Map<String, WidgetViewHolderFactory>` (ключ — `widget.type`
 * или зарезервированный ключ структурного элемента).
 *
 * Живёт ВНУТРИ [WorkflowComponent] (а не в `AppComponent`): ядровые виджеты помечены [CoreWidgets],
 * вклады фич приходят отдельно как [FeatureWidgets] через `@BindsInstance`. [widgetRegistry] сливает
 * их в итоговую карту, которую отдаёт [az.less.core.workflow.api.WorkflowFeatureApi]. Так корень не
 * тянет внутренние рендереры/форматтеры движка.
 */
@Module
interface WorkflowWidgetsModule {

    @Binds @IntoMap @CoreWidgets @WidgetTypeKey("FIELDSET")
    fun fieldset(impl: FieldSetViewHolderFactory): WidgetViewHolderFactory

    @Binds @IntoMap @CoreWidgets @WidgetTypeKey("SUMMARY")
    fun summary(impl: SummaryViewHolderFactory): WidgetViewHolderFactory

    @Binds @IntoMap @CoreWidgets @WidgetTypeKey("BANNER")
    fun banner(impl: BannerViewHolderFactory): WidgetViewHolderFactory

    @Binds @IntoMap @CoreWidgets @WidgetTypeKey(ScreenItem.TYPE_HEADER)
    fun header(impl: HeaderViewHolderFactory): WidgetViewHolderFactory

    @Binds @IntoMap @CoreWidgets @WidgetTypeKey(ScreenItem.TYPE_STEPPER)
    fun stepper(impl: StepperViewHolderFactory): WidgetViewHolderFactory

    @Binds @IntoMap @CoreWidgets @WidgetTypeKey(ScreenItem.TYPE_EVENT)
    fun event(impl: EventViewHolderFactory): WidgetViewHolderFactory

    companion object {
        /** Итоговый реестр: ядровые виджеты + вклады фич (first-key ядра, фичи добавляют свои типы). */
        @Provides
        fun widgetRegistry(
            @CoreWidgets core: Map<String, @JvmSuppressWildcards WidgetViewHolderFactory>,
            @FeatureWidgets feature: Map<String, @JvmSuppressWildcards WidgetViewHolderFactory>,
        ): Map<String, @JvmSuppressWildcards WidgetViewHolderFactory> = core + feature
    }
}
