package az.less.feature.settings.impl.di

import az.less.core.di.BaseFeatureHolder
import az.less.core.di.FeatureContainer
import az.less.core.network.api.NetworkCoreApi
import az.less.feature.settings.api.SettingsFeatureApi

internal class SettingsHolder(
    container: FeatureContainer,
) : BaseFeatureHolder<SettingsFeatureApi>(container) {

    override fun buildFeature(): SettingsFeatureApi =
        DaggerSettingsComponent.factory()
            .create(getDependency(NetworkCoreApi::class.java))
}
