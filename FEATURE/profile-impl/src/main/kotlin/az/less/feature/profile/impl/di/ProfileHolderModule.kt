package az.less.feature.profile.impl.di

import az.less.core.di.FeatureContainer
import az.less.core.di.FeatureHolder
import az.less.feature.profile.api.ProfileFeatureApi
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Singleton

/** Регистрирует холдер profile в общую `Map<Class, FeatureHolder>`. */
@Module
interface ProfileHolderModule {

    companion object {
        @Provides
        @Singleton
        @IntoMap
        @ClassKey(ProfileFeatureApi::class)
        fun provideProfileHolder(container: FeatureContainer): FeatureHolder<*> =
            ProfileHolder(container)
    }
}
