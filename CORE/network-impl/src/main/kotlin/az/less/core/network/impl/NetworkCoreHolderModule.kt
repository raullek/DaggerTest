package az.less.core.network.impl

import az.less.core.di.FeatureContainer
import az.less.core.di.FeatureHolder
import az.less.core.network.api.NetworkCoreApi
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Singleton

/** Регистрирует холдер сетевого ядра в общую `Map<Class, FeatureHolder>`. */
@Module
interface NetworkCoreHolderModule {

    companion object {
        @Provides
        @Singleton
        @IntoMap
        @ClassKey(NetworkCoreApi::class)
        fun provideNetworkCoreHolder(container: FeatureContainer): FeatureHolder<*> =
            NetworkCoreHolder(container)
    }
}
