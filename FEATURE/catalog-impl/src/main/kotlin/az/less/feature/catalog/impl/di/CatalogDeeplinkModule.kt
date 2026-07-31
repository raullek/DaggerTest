package az.less.feature.catalog.impl.di

import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.deeplink.api.DeeplinkHandlerEntry
import az.less.core.deeplink.api.Deeplinks
import az.less.core.deeplink.api.step.LaunchFeatureDeeplinkStep
import az.less.core.di.api
import az.less.feature.catalog.api.CatalogFeatureApi
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

/**
 * Вклад фичи catalog в ядро диплинков: регистрирует обработчик для
 * `app-app://az.less.daggertest/catalog` (и `daggertest://catalog`).
 *
 * Ядро о catalog ничего не знает — связь только через `@IntoSet` (Dagger соберёт
 * все такие записи в AppComponent) и ленивую лямбду, которая достаёт лаунчер фичи
 * из `DI` лишь в момент обработки.
 */
@Module
object CatalogDeeplinkModule {

    @Provides
    @IntoSet
    fun provideCatalogDeeplinkEntry(): DeeplinkHandlerEntry =
        DeeplinkHandlerEntry(uri = Deeplinks.feature(PATH)) {
            val factory = api<DeeplinkFeatureApi>().stepFactory()
            factory.handlerBuilder()
                .addStep(
                    LaunchFeatureDeeplinkStep(factory.currentActivityProvider()) { fragmentManager ->
                        api<CatalogFeatureApi>().launcher().launch(fragmentManager)
                    },
                )
                .build()
        }

    private const val PATH = "catalog"
}
