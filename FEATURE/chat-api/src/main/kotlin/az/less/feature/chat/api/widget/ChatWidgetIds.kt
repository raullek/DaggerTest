package az.less.feature.chat.api.widget

/**
 * Известные строковые идентификаторы виджетов чата — ключи `@StringKey` мультибиндингов.
 * Владелец константы — фича-поставщик виджета;
 * сервер оперирует этими же строками (server-driven ключи).
 */
object ChatWidgetIds {
    /** Карточка профиля (владелец — feature-profile). */
    const val PROFILE_CARD = "PROFILE_CARD"

    /** Витрина товаров (владелец — feature-catalog). */
    const val CATALOG_SHOWCASE = "CATALOG_SHOWCASE"

    /** Быстрый перевод (владелец — feature-transfer). */
    const val TRANSFER_ACTION = "TRANSFER_ACTION"
}
