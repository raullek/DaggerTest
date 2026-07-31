package az.less.core.network.impl

import az.less.core.di.PerFeature
import az.less.core.network.api.NetworkCoreApi
import dagger.Component

/**
 * Граф сетевого ядра. Лист графа зависимостей — ни от кого не зависит.
 * Реализует [NetworkCoreApi], поэтому сам компонент и есть API фичи.
 */
@PerFeature
@Component(modules = [NetworkModule::class])
internal interface NetworkCoreComponent : NetworkCoreApi {

    @Component.Factory
    interface Factory {
        fun create(): NetworkCoreComponent
    }
}
