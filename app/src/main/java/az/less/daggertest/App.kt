package az.less.daggertest

import android.app.Application
import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.di.DI
import az.less.core.di.FeatureContainerImpl
import az.less.core.di.api
import az.less.daggertest.di.DaggerAppComponent

/**
 * Точка инициализации DI.
 *
 * Создаём пустой контейнер, затем наполняем его мапой холдеров из AppComponent
 * (контейнер передаём холдерам через @BindsInstance), и регистрируем в [DI].
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val container = FeatureContainerImpl().init { featureContainer ->
            DaggerAppComponent.factory()
                .create(featureContainer)
                .featureHolders()
        }
        DI.initialize(container)

        // Лениво поднимаем ядро диплинков и подписываем его на жизненный цикл
        // Activity (нужно для навигации по диплинку — см. CurrentActivityProvider).
        // Сами обработчики фич всё равно строятся лениво, только при совпадении Uri.
        api<DeeplinkFeatureApi>().attach(this)
    }
}



