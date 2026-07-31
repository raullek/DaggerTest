package az.less.core.network.api

import az.less.core.di.FeatureApi
import retrofit2.Retrofit

/** Публичное API сетевого ядра. Отдаёт общий Retrofit фичам. */
interface NetworkCoreApi : FeatureApi {
    fun retrofit(): Retrofit
}
