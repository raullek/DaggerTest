package az.less.core.deeplink.impl.di

import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.deeplink.api.DeeplinkHandlerEntry
import az.less.core.deeplink.api.DeeplinkUriMatcher
import az.less.core.di.BaseFeatureHolder
import az.less.core.di.FeatureContainer

/**
 * Холдер ядра диплинков: лениво строит [DeeplinkComponent], отдавая ему
 * агрегированные вклады фич. Аналог `NetworkCoreHolder`, но с мультибиндингами.
 */
internal class DeeplinkCoreHolder(
    container: FeatureContainer,
    private val entries: Set<@JvmSuppressWildcards DeeplinkHandlerEntry>,
    private val matchers: Set<@JvmSuppressWildcards DeeplinkUriMatcher>,
) : BaseFeatureHolder<DeeplinkFeatureApi>(container) {

    override fun buildFeature(): DeeplinkFeatureApi =
        DaggerDeeplinkComponent.factory()
            .create(entries, matchers)
            .deeplinkApi()
}
