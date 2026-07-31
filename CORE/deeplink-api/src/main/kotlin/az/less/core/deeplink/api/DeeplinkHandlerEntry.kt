package az.less.core.deeplink.api

import android.net.Uri

/**
 * Запись-регистрация: «этот Uri обрабатывает вот этот (лениво создаваемый) обработчик».
 *
 * Фичи кладут такие записи в общий `Set<DeeplinkHandlerEntry>` через Dagger
 * `@IntoSet` (агрегируется в AppComponent). [handlerProvider] — ленивая фабрика:
 * вызывается только при совпадении Uri, поэтому граф обработчика (шаги, экраны)
 * не строится заранее. Внутри лямбды фича достаёт свои объекты через `DI`.
 *
 * @param supportNestedPath матчить ли вложенные пути (`.../products/card` → `.../products`).
 * @param supportAdditionalParams допускать ли лишние query-параметры сверх ключевых.
 */
class DeeplinkHandlerEntry(
    val uri: Uri,
    val supportNestedPath: Boolean = false,
    val supportAdditionalParams: Boolean = true,
    val handlerProvider: () -> DeeplinkHandler,
)
