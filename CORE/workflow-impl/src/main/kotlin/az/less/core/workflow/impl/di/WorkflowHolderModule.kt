package az.less.core.workflow.impl.di

import az.less.core.di.FeatureContainer
import az.less.core.di.FeatureHolder
import az.less.core.workflow.api.WorkflowFeatureApi
import az.less.core.workflow.api.widget.FeatureWidgets
import az.less.core.workflow.api.widget.WidgetViewHolderFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds
import javax.inject.Singleton

/**
 * Регистрирует холдер ядра workflow в общую `Map<Class, FeatureHolder>` и собирает в `AppComponent`
 * ТОЛЬКО вклады виджетов от фич ([FeatureWidgets]) — внутренние реестры движка (поля/форматтеры/
 * валидаторы/ядровые виджеты) теперь строятся внутри [WorkflowComponent], а не в корне.
 *
 * [featureWidgets] объявляет мультибиндинг, чтобы карта существовала даже когда ни одна фича не
 * добавила свой виджет. Включается в `app/AppHolderModule` рядом с модулями-вкладами фич.
 */
@Module
interface WorkflowHolderModule {

    /** Гарантирует существование карты вкладов фич (возможно пустой). */
    @Multibinds
    @FeatureWidgets
    fun featureWidgets(): Map<String, @JvmSuppressWildcards WidgetViewHolderFactory>

    companion object {
        @Provides
        @Singleton
        @IntoMap
        @ClassKey(WorkflowFeatureApi::class)
        fun provideWorkflowHolder(
            container: FeatureContainer,
            @FeatureWidgets featureWidgets: Map<String, @JvmSuppressWildcards WidgetViewHolderFactory>,
        ): FeatureHolder<*> = WorkflowHolder(container, featureWidgets)
    }
}
