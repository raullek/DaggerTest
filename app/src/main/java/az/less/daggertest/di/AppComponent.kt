package az.less.daggertest.di

import az.less.core.di.FeatureContainer
import az.less.core.di.FeatureHolder
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Корневой компонент. Его единственная задача — отдать `Map<Class, FeatureHolder>`.
 * Реализаций фич в нём нет — каждая фича строит свой граф в собственном холдере.
 */
@Singleton
@Component(modules = [AppHolderModule::class, AppDeeplinkModule::class])
interface AppComponent {

    fun featureHolders(): Map<Class<*>, @JvmSuppressWildcards FeatureHolder<*>>


    @Component.Factory
    interface Factory {
        // FeatureContainer прокидывается в холдеры через @BindsInstance — они через
        // него достают чужие зависимости (getDependency).
        fun create(@BindsInstance container: FeatureContainer): AppComponent
    }
}
