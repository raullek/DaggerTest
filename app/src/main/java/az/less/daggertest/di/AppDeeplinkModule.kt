package az.less.daggertest.di

import az.less.core.deeplink.impl.di.DeeplinkCoreHolderModule
import az.less.feature.catalog.impl.di.CatalogDeeplinkModule
import az.less.feature.profile.impl.di.ProfileDeeplinkModule
import az.less.feature.settings.impl.di.SettingsDeeplinkModule
import dagger.Module

/**
 * Точка сборки обработки диплинков на уровне приложения.
 *
 * Включает ядро диплинков (его холдер + `@Multibinds`-затравки `Set<DeeplinkHandlerEntry>`)
 * и вклады фич (`@IntoSet`). Dagger агрегирует все записи в единый `Set` в AppComponent и
 * прокидывает его в `DeeplinkCoreHolder`. Вынесено из [AppHolderModule], чтобы тот отвечал
 * только за регистрацию FeatureHolder'ов и реестров SDUI.
 */
@Module(
    includes = [
        // Ядро диплинков: его холдер + @Multibinds-затравки Set<DeeplinkHandlerEntry>.
        DeeplinkCoreHolderModule::class,
        // Вклады фич в Set<DeeplinkHandlerEntry> (@IntoSet) — агрегируются здесь, в AppComponent.
        ProfileDeeplinkModule::class,
        CatalogDeeplinkModule::class,
        SettingsDeeplinkModule::class,
    ],
)
interface AppDeeplinkModule
