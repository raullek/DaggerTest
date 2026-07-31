package az.less.feature.transfer.impl.di

import az.less.core.di.BaseFeatureHolder
import az.less.core.di.FeatureContainer
import az.less.feature.transfer.api.TransferFeatureApi

internal class TransferHolder(
    container: FeatureContainer,
) : BaseFeatureHolder<TransferFeatureApi>(container) {

    override fun buildFeature(): TransferFeatureApi =
        DaggerTransferComponent.factory().create()
}
