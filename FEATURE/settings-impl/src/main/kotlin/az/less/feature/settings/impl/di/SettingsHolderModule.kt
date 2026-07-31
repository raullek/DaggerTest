package az.less.feature.settings.impl.di

import az.less.core.di.FeatureContainer
import az.less.core.di.FeatureHolder
import az.less.feature.settings.api.SettingsFeatureApi
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
interface SettingsHolderModule {

    companion object {
        @Provides
        @Singleton
        @IntoMap
        @ClassKey(SettingsFeatureApi::class)
        fun provideSettingsHolder(container: FeatureContainer): FeatureHolder<*> =
            SettingsHolder(container)
    }
}
