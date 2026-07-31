package az.less.feature.profile.impl.di

import az.less.core.di.PerFeature
import az.less.feature.profile.api.ProfileLauncher
import az.less.feature.profile.api.ProfileRepository
import az.less.feature.profile.impl.data.ProfileRepositoryImpl
import az.less.feature.profile.impl.data.ProfileService
import az.less.feature.profile.impl.navigation.ProfileLauncherImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
internal interface ProfileModule {

    @Binds
    @PerFeature
    fun bindLauncher(impl: ProfileLauncherImpl): ProfileLauncher

    @Binds
    fun bindRepository(impl: ProfileRepositoryImpl): ProfileRepository

    companion object {
        /** Из Retrofit (его отдаёт NetworkCoreApi) собираем API-интерфейс data-слоя. */
        @Provides
        @PerFeature
        fun provideService(retrofit: Retrofit): ProfileService =
            retrofit.create(ProfileService::class.java)
    }
}
