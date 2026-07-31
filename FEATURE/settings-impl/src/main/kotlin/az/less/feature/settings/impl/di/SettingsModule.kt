package az.less.feature.settings.impl.di

import az.less.core.di.PerFeature
import az.less.feature.settings.api.SettingsLauncher
import az.less.feature.settings.api.SettingsRepository
import az.less.feature.settings.impl.data.SettingsRepositoryImpl
import az.less.feature.settings.impl.data.SettingsService
import az.less.feature.settings.impl.navigation.SettingsLauncherImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
internal interface SettingsModule {

    @Binds
    @PerFeature
    fun bindLauncher(impl: SettingsLauncherImpl): SettingsLauncher

    @Binds
    @PerFeature
    fun bindRepository(impl: SettingsRepositoryImpl): SettingsRepository

    companion object {
        @Provides
        @PerFeature
        fun provideService(retrofit: Retrofit): SettingsService =
            retrofit.create(SettingsService::class.java)
    }
}
