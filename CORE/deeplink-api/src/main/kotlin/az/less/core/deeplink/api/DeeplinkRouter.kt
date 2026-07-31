package az.less.core.deeplink.api

import android.net.Uri

/**
 * Точка внутреннего запуска диплинка из кода (без Intent/Activity): фича может
 * сама инициировать переход по канонической ссылке, не зная целевую фичу.
 */
interface DeeplinkRouter {

    /** Открыть внутренний диплинк (`app-app://...`). */
    fun open(uri: Uri)

    /** Открыть с явным признаком внешнего источника. */
    fun open(deeplinkUri: DeeplinkUri)
}
