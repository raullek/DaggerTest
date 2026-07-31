package az.less.core.deeplink.impl.di

import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.deeplink.api.DeeplinkHandlerEntry
import az.less.core.deeplink.api.DeeplinkUriMatcher
import az.less.core.di.FeatureContainer
import az.less.core.di.FeatureHolder
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds
import javax.inject.Singleton

/**
 * Регистрирует холдер ядра диплинков в общую `Map<Class, FeatureHolder>` и
 * объявляет «затравки» мультибиндингов вкладов фич.
 *
 * Этот модуль включается в AppComponent (через AppHolderModule). Туда же входят
 * фичевые модули с `@IntoSet DeeplinkHandlerEntry` — Dagger агрегирует их в единый
 * `Set` и прокидывает в [provideDeeplinkCoreHolder]. Если ни одна фича не
 * подключила диплинков, [Multibinds] гарантирует пустой (а не отсутствующий) Set.
 */
@Module
interface DeeplinkCoreHolderModule {

    @Multibinds
    fun deeplinkHandlerEntries(): Set<@JvmSuppressWildcards DeeplinkHandlerEntry>

    @Multibinds
    fun deeplinkUriMatchers(): Set<@JvmSuppressWildcards DeeplinkUriMatcher>

    companion object {
        @Provides
        @Singleton
        @IntoMap
        @ClassKey(DeeplinkFeatureApi::class)
        fun provideDeeplinkCoreHolder(
            container: FeatureContainer,
            entries: Set<@JvmSuppressWildcards DeeplinkHandlerEntry>,
            matchers: Set<@JvmSuppressWildcards DeeplinkUriMatcher>,
        ): FeatureHolder<*> = DeeplinkCoreHolder(container, entries, matchers)
    }
}
