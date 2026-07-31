package az.less.core.deeplink.impl.storage

import android.net.Uri
import az.less.core.deeplink.api.DeeplinkHandler
import az.less.core.deeplink.api.DeeplinkHandlerEntry
import az.less.core.deeplink.api.DeeplinkUri
import az.less.core.deeplink.api.DeeplinkUriMatcher
import az.less.core.deeplink.api.Deeplinks

/**
 * Хранилище и матчинг обработчиков — двухступенчатый поиск:
 *
 * 1. Точное совпадение по каноническому ключу — O(1).
 * 2. Если включён [DeeplinkHandlerEntry.supportNestedPath] — поиск «родителя»:
 *    отрезаем по одному сегменту (`products/card/credit` → `products/card` → …).
 * 3. Fallback — перебор [DeeplinkUriMatcher] (динамические Uri).
 *
 * **Канонический ключ** делает внутреннюю и внешнюю схемы эквивалентными:
 * и `app-app://az.less.daggertest/catalog`, и
 * `daggertest://catalog` сводятся к токену `catalog`. Запрос (`?ok=true`)
 * в ключе игнорируется, но остаётся в исходном [DeeplinkUri] для шагов.
 *
 * Возвращает ленивый провайдер: сам обработчик строится позже, только при обработке.
 */
class DeeplinkHandlerStorage(
    entries: Set<DeeplinkHandlerEntry>,
    private val matchers: Set<DeeplinkUriMatcher>,
) {
    private val exact: Map<String, DeeplinkHandlerEntry> =
        entries.associateBy { keyOf(it.uri) }

    private val nestedEntries: List<DeeplinkHandlerEntry> =
        entries.filter { it.supportNestedPath }

    fun hasHandler(deeplinkUri: DeeplinkUri): Boolean = findProvider(deeplinkUri.uri) != null

    fun findHandler(deeplinkUri: DeeplinkUri): DeeplinkHandler? = findProvider(deeplinkUri.uri)?.invoke()

    private fun findProvider(uri: Uri): (() -> DeeplinkHandler)? {
        val tokens = tokensOf(uri)
        exact[tokens.joinToString("/")]?.let { return it.handlerProvider }
        findNestedProvider(tokens)?.let { return it }
        return matchers.firstOrNull { it.isMatched(uri) }?.handlerProvider
    }

    /** Поиск обработчика «родительского» пути для вложенных Uri. */
    private fun findNestedProvider(tokens: List<String>): (() -> DeeplinkHandler)? {
        if (nestedEntries.isEmpty() || tokens.size <= 1) return null
        var parent = tokens.dropLast(1)
        while (parent.isNotEmpty()) {
            val key = parent.joinToString("/")
            nestedEntries.firstOrNull { keyOf(it.uri) == key }?.let { return it.handlerProvider }
            parent = parent.dropLast(1)
        }
        return null
    }

    private fun keyOf(uri: Uri): String = tokensOf(uri).joinToString("/")

    /**
     * Сегменты-«токены» Uri независимо от схемы. Для внешней схемы значимая часть
     * сидит в authority (+ path), для внутренней — в path (authority — это applicationId).
     */
    private fun tokensOf(uri: Uri): List<String> {
        val scheme = uri.scheme?.lowercase()
        val raw = if (scheme == Deeplinks.EXTERNAL_SCHEME) {
            buildList {
                uri.authority?.let { add(it) }
                addAll(uri.pathSegments)
            }
        } else {
            uri.pathSegments
        }
        return raw.map { it.lowercase().trim('/') }.filter { it.isNotEmpty() }
    }
}
