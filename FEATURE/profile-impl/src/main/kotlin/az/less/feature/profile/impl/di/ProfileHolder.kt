package az.less.feature.profile.impl.di

import az.less.core.di.BaseFeatureHolder
import az.less.core.di.FeatureContainer
import az.less.core.network.api.NetworkCoreApi
import az.less.feature.profile.api.ProfileFeatureApi

/**
 * Холдер фичи profile: лениво строит граф, доставая чужой [NetworkCoreApi]
 * из контейнера через getDependency().
 */
internal class ProfileHolder(
    container: FeatureContainer,
) : BaseFeatureHolder<ProfileFeatureApi>(container) {

    override fun buildFeature(): ProfileFeatureApi =
        DaggerProfileComponent.factory()
            .create(getDependency(NetworkCoreApi::class.java))
}
