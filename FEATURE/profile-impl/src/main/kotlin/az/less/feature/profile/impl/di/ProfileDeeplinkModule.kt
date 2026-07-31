package az.less.feature.profile.impl.di

import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.deeplink.api.DeeplinkHandlerEntry
import az.less.core.deeplink.api.Deeplinks
import az.less.core.deeplink.api.step.LaunchFeatureDeeplinkStep
import az.less.core.di.api
import az.less.feature.profile.api.ProfileFeatureApi
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

/** Вклад фичи profile: обработчик `app-app://az.less.daggertest/profile`. */
@Module
object ProfileDeeplinkModule {

    @Provides
    @IntoSet
    fun provideProfileDeeplinkEntry(): DeeplinkHandlerEntry =
        DeeplinkHandlerEntry(uri = Deeplinks.feature(PATH)) {
            val factory = api<DeeplinkFeatureApi>().stepFactory()
            factory.handlerBuilder()
                .addStep(
                    LaunchFeatureDeeplinkStep(factory.currentActivityProvider()) { fragmentManager ->
                        api<ProfileFeatureApi>().launcher().launch(fragmentManager)
                    },
                )
                .build()
        }

    private const val PATH = "profile"
}
