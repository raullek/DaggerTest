package az.less.feature.transfer.impl.di

import az.less.core.di.FeatureContainer
import az.less.core.di.FeatureHolder
import az.less.feature.transfer.api.TransferFeatureApi
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
interface TransferHolderModule {

    companion object {
        @Provides
        @Singleton
        @IntoMap
        @ClassKey(TransferFeatureApi::class)
        fun provideTransferHolder(container: FeatureContainer): FeatureHolder<*> =
            TransferHolder(container)
    }
}
