package az.less.feature.settings.impl.di

import az.less.core.di.PerFeature
import az.less.core.network.api.NetworkCoreApi
import az.less.feature.settings.api.SettingsFeatureApi
import dagger.Component

@PerFeature
@Component(
    dependencies = [NetworkCoreApi::class],
    modules = [SettingsModule::class],
)
internal interface SettingsComponent : SettingsFeatureApi {

    @Component.Factory
    interface Factory {
        fun create(networkCoreApi: NetworkCoreApi): SettingsComponent
    }
}
