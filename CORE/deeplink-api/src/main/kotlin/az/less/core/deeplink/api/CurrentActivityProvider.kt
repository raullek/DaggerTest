package az.less.core.deeplink.api

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

/**
 * Отдаёт текущую видимую Activity — навигационным шагам нужен «живой» хост, чтобы
 * открыть экран фичи (реализация в :impl слушает Application.ActivityLifecycleCallbacks).
 */
interface CurrentActivityProvider {

    /** Текущая resumed-Activity или null. */
    val current: Activity?

    /** Поток текущей Activity (для подписки/ожидания). */
    val activityFlow: StateFlow<Activity?>

    /**
     * Ждёт первую resumed-Activity, удовлетворяющую [predicate]. Если подходящая
     * уже активна — возвращается сразу. Используется навигационными шагами, чтобы
     * дождаться хоста после старта приложения по диплинку.
     */
    suspend fun awaitActivity(predicate: (Activity) -> Boolean = { true }): Activity
}
