package az.less.feature.profile.impl.di

import az.less.core.di.PerFeature
import az.less.core.network.api.NetworkCoreApi
import az.less.feature.profile.api.ProfileFeatureApi
import dagger.Component

/**
 * Граф фичи profile. Зависит от [NetworkCoreApi] (даёт Retrofit), data-биндинги
 * в [ProfileModule]. Реализует [ProfileFeatureApi] — компонент и есть API фичи.
 */
@PerFeature
@Component(
    dependencies = [NetworkCoreApi::class],
    modules = [ProfileModule::class],
)
internal interface ProfileComponent : ProfileFeatureApi {

    @Component.Factory
    interface Factory {
        fun create(networkCoreApi: NetworkCoreApi): ProfileComponent
    }
}
