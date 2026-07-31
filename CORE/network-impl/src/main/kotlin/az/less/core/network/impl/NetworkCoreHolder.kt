package az.less.core.network.impl

import az.less.core.di.BaseFeatureHolder
import az.less.core.di.FeatureContainer
import az.less.core.network.api.NetworkCoreApi

/** Холдер сетевого ядра: лениво строит [NetworkCoreComponent]. */
internal class NetworkCoreHolder(
    container: FeatureContainer,
) : BaseFeatureHolder<NetworkCoreApi>(container) {

    override fun buildFeature(): NetworkCoreApi =
        DaggerNetworkCoreComponent.factory().create()
}
