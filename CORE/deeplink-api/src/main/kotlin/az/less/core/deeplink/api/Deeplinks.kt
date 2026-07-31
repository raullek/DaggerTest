package az.less.core.deeplink.api

import android.net.Uri

/**
 * Константы схемы и фабрика канонических Uri — единый словарь диплинков:
 * фичи строят свои Uri через [feature], а ключи держат у себя.
 *
 * Внутренняя схема — `app-app://az.less.daggertest/<path>`. Плюс внешняя
 * `daggertest://<path>` для тестов с adb
 * (`adb shell am start -a android.intent.action.VIEW -d daggertest://catalog`).
 */
object Deeplinks {

    const val INTERNAL_SCHEME = "app-app"
    const val EXTERNAL_SCHEME = "daggertest"
    const val AUTHORITY = "az.less.daggertest"

    /** Канонический внутренний Uri для пути фичи: `app-app://az.less.daggertest/<path>`. */
    fun feature(path: String): Uri = Uri.Builder()
        .scheme(INTERNAL_SCHEME)
        .authority(AUTHORITY)
        .path(path)
        .build()

    /** Внешний аналог того же пути: `daggertest://<path>`. */
    fun external(path: String): Uri = Uri.parse("$EXTERNAL_SCHEME://${path.trimStart('/')}")
}
