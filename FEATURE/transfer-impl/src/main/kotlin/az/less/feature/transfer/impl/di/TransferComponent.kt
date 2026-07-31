package az.less.feature.transfer.impl.di

import az.less.core.di.PerFeature
import az.less.feature.transfer.api.TransferFeatureApi
import dagger.Component

/**
 * Граф фичи перевода. Лист графа — фича не зависит ни от кого через Dagger (ядро workflow берётся
 * из `DI`-фасада в рантайме). Реализует [TransferFeatureApi].
 */
@PerFeature
@Component(modules = [TransferModule::class])
internal interface TransferComponent : TransferFeatureApi {

    @Component.Factory
    interface Factory {
        fun create(): TransferComponent
    }
}
