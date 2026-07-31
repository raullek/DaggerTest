package az.less.feature.transfer.impl.di

import az.less.core.di.PerFeature
import az.less.feature.transfer.api.TransferLauncher
import az.less.feature.transfer.impl.navigation.TransferLauncherImpl
import dagger.Binds
import dagger.Module

@Module
internal interface TransferModule {

    @Binds
    @PerFeature
    fun bindLauncher(impl: TransferLauncherImpl): TransferLauncher
}
