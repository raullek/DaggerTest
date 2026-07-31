package az.less.core.workflow.compose.impl.di

import az.less.core.di.FeatureContainer
import az.less.core.di.FeatureHolder
import az.less.core.workflow.compose.api.WorkflowComposeFeatureApi
import az.less.core.workflow.compose.api.widget.FeatureWidgets
import az.less.core.workflow.compose.api.widget.WidgetRegistry
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds
import javax.inject.Singleton

/**
 * Регистрирует холдер ядра BDUI-Compose в общую `Map<Class, FeatureHolder>` и собирает в AppComponent
 * ТОЛЬКО вклады виджетов от фич ([FeatureWidgets]) — внутренние реестры движка (валидаторы/ядровые
 * виджеты) строятся внутри [WorkflowComposeComponent], а не в корне.
 *
 * [featureWidgets] объявляет мультибиндинг, чтобы набор существовал даже когда ни одна фича не
 * добавила свой виджет. Включается в `app/AppHolderModule`.
 */
@Module
interface WorkflowComposeHolderModule {

    /** Гарантирует существование набора вкладов фич (возможно пустого). */
    @Multibinds
    @FeatureWidgets
    fun featureWidgets(): Set<@JvmSuppressWildcards WidgetRegistry>

    companion object {
        @Provides
        @Singleton
        @IntoMap
        @ClassKey(WorkflowComposeFeatureApi::class)
        fun provideHolder(
            container: FeatureContainer,
            @FeatureWidgets featureWidgets: Set<@JvmSuppressWildcards WidgetRegistry>,
        ): FeatureHolder<*> = WorkflowComposeHolder(container, featureWidgets)
    }
}
