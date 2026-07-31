package az.less.core.deeplink.api

import android.net.Uri

/**
 * Динамический матчер для Uri, которые нельзя задать константой (например, с
 * переменным id в пути): предикат + ленивый
 * провайдер обработчика. Фичи кладут наследников в `Set<DeeplinkUriMatcher>`
 * через `@IntoSet`. Используется как fallback, когда не нашлось точного совпадения.
 */
abstract class DeeplinkUriMatcher(
    val handlerProvider: () -> DeeplinkHandler,
) {
    abstract fun isMatched(uri: Uri): Boolean
}
