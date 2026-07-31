package az.less.core.deeplink.api.step

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import az.less.core.deeplink.api.CurrentActivityProvider
import az.less.core.deeplink.api.DeeplinkStep
import az.less.core.deeplink.api.DeeplinkUri
import az.less.core.deeplink.api.HandlingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Терминальный навигационный шаг: дожидается активного хоста (через
 * [CurrentActivityProvider]) и запускает экран фичи её собственным лаунчером.
 *
 * Шаг рассчитан на навигацию проекта (фрагменты + per-feature лаунчеры) и не
 * знает о конкретной фиче — получает готовую лямбду [launch], которую фича
 * строит из своего `XxxLauncher`.
 */
class LaunchFeatureDeeplinkStep(
    private val currentActivityProvider: CurrentActivityProvider,
    private val launch: (FragmentManager) -> Unit,
) : DeeplinkStep {

    override suspend fun execute(deeplinkUri: DeeplinkUri): HandlingResult {
        // Ждём, пока появится FragmentActivity-хост (DeeplinkActivity исключена
        // в провайдере), затем запускаем переход на главном потоке.
        val host = currentActivityProvider.awaitFragmentHost()
        return withContext(Dispatchers.Main.immediate) {
            if (host.isFinishing || host.isDestroyed) {
                HandlingResult.Failed("Host activity is gone")
            } else {
                launch(host.supportFragmentManager)
                HandlingResult.Success
            }
        }
    }
}

/** Удобный хелпер: дождаться именно [FragmentActivity]. */
internal suspend fun CurrentActivityProvider.awaitFragmentHost(): FragmentActivity =
    awaitActivity { it is FragmentActivity } as FragmentActivity
