package az.less.core.deeplink.api

import android.net.Uri
import android.os.Bundle

/**
 * Обёртка над входящим [Uri] — то, с чем работает весь конвейер: несёт сам Uri,
 * флаг внешний/внутренний источник и доп. аргументы (Bundle из Intent).
 *
 * - [isExternal] — пришёл снаружи (браузер, пуш, другое приложение) или изнутри
 *   (внутренний переход через [DeeplinkRouter]). Шаги вроде "только для внешних"
 *   опираются на этот флаг.
 */
data class DeeplinkUri(
    val uri: Uri,
    val isExternal: Boolean,
    val additionalArgs: Bundle = Bundle.EMPTY,
) {
    companion object {
        fun external(uri: Uri, args: Bundle = Bundle.EMPTY): DeeplinkUri =
            DeeplinkUri(uri, isExternal = true, additionalArgs = args)

        fun internal(uri: Uri, args: Bundle = Bundle.EMPTY): DeeplinkUri =
            DeeplinkUri(uri, isExternal = false, additionalArgs = args)
    }
}
