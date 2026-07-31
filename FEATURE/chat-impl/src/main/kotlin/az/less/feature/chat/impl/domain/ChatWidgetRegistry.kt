package az.less.feature.chat.impl.domain

import az.less.feature.chat.api.action.ChatQuickAction
import az.less.feature.chat.api.widget.ChatWidgetComposer
import az.less.feature.chat.api.widget.ChatWidgetViewHolderFactory

/**
 * Реестр вкладов фич — контейнер над «сырыми» Dagger-агрегатами, ЕДИНСТВЕННОЕ место, где чат
 * их трогает (вместо инжекта Map по всему коду).
 *
 * Стратегии диспатча:
 * - виджеты — key-lookup по server-driven id, `null` = «клиент не знает виджет» → фолбэк рендера;
 * - быстрые действия — iterate-all с фильтром доступности.
 */
class ChatWidgetRegistry(
    private val xmlFactories: Map<String, ChatWidgetViewHolderFactory>,
    private val composers: Map<String, ChatWidgetComposer>,
    private val quickActions: Set<ChatQuickAction>,
) {

    /** XML-фабрика виджета или null, если ни одна фича не зарегистрировала такой id. */
    fun xmlFactory(widgetId: String): ChatWidgetViewHolderFactory? = xmlFactories[widgetId]

    /** Compose-рендер виджета или null (фолбэк-заглушку рисует экран). */
    fun composer(widgetId: String): ChatWidgetComposer? = composers[widgetId]

    /** Стабильный список известных XML-виджетов — для view type'ов адаптера. */
    fun xmlWidgetIds(): List<String> = xmlFactories.keys.sorted()

    /** Доступные быстрые действия в заданном фичами порядке. */
    fun quickActions(): List<ChatQuickAction> =
        quickActions.filter { it.isAvailable() }.sortedBy { it.order }
}
