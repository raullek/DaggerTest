package az.less.core.deeplink.impl.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import az.less.core.deeplink.api.DeeplinkFeatureApi
import az.less.core.deeplink.api.DeeplinkUri
import az.less.core.di.DI

/**
 * Невидимая диспетчерская Activity — единственная точка входа внешних ссылок
 * (intent-filter'ы в манифесте):
 *
 * 1. достаёт Uri и доп. аргументы из Intent;
 * 2. поднимает UI-хост (launcher-Activity приложения), чтобы навигационным шагам
 *    было куда открыть экран фичи;
 * 3. отдаёт Uri роутеру ядра (обработка идёт на scope ядра и переживает закрытие
 *    этой Activity);
 * 4. сразу завершается (`noHistory`).
 */
class DeeplinkActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        if (data == null) {
            Log.w(TAG, "DeeplinkActivity started without data")
            finish()
            return
        }

        val isInternal = intent.getBooleanExtra(EXTRA_INTERNAL, false)
        val args = intent.getBundleExtra(EXTRA_ARGS) ?: Bundle.EMPTY
        val deeplinkUri =
            if (isInternal) DeeplinkUri.internal(data, args) else DeeplinkUri.external(data, args)

        val deeplinkApi = DI.getFeature(DeeplinkFeatureApi::class.java)

        // Поднимаем хост, если запустились «снаружи» в новую задачу.
        startHostIfNeeded()

        // Обработка — на scope ядра (через роутер), не на этой завершающейся Activity.
        deeplinkApi.router().open(deeplinkUri)

        finish()
    }

    /** Запускает launcher-Activity приложения (MainActivity из :entry) без жёсткой связи. */
    private fun startHostIfNeeded() {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: return
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(launchIntent)
    }

    companion object {
        private const val TAG = "Deeplink"
        const val EXTRA_INTERNAL = "az.less.deeplink.EXTRA_INTERNAL"
        const val EXTRA_ARGS = "az.less.deeplink.EXTRA_ARGS"

        /** Intent для внутреннего запуска диплинка через эту Activity. */
        fun createIntent(context: Context, uri: Uri, args: Bundle? = null): Intent =
            Intent(context, DeeplinkActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                data = uri
                putExtra(EXTRA_INTERNAL, true)
                if (args != null) putExtra(EXTRA_ARGS, args)
            }
    }
}
