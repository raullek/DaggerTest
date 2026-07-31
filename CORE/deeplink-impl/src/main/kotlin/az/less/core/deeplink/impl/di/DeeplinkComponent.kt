package az.less.core.deeplink.impl.di

import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.deeplink.api.DeeplinkHandlerEntry
import az.less.core.deeplink.api.DeeplinkUriMatcher
import dagger.BindsInstance
import dagger.Component

/**
 * Граф ядра диплинков. В отличие от фич, не зависит ни от кого (лист графа), но
 * принимает извне агрегированные мультибиндинги — `Set<DeeplinkHandlerEntry>` и
 * `Set<DeeplinkUriMatcher>`, собранные Dagger'ом в AppComponent из всех фич
 * через `@IntoSet` — так вклады фич попадают в ядро при раздельных
 * per-feature компонентах проекта.
 */
@DeeplinkScope
@Component(modules = [DeeplinkModule::class])
internal interface DeeplinkComponent {

    fun deeplinkApi(): DeeplinkFeatureApi

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance entries: Set<@JvmSuppressWildcards DeeplinkHandlerEntry>,
            @BindsInstance matchers: Set<@JvmSuppressWildcards DeeplinkUriMatcher>,
        ): DeeplinkComponent
    }
}
