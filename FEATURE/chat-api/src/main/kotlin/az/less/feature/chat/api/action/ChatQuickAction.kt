package az.less.feature.chat.api.action

import androidx.fragment.app.FragmentActivity

/**
 * Быстрое действие в чате (чип над полем ввода): каждая фича кладёт свою «визитку»
 * в общий `Set<ChatQuickAction>` через `@Provides @IntoSet`,
 * а чат просто фильтрует по [isAvailable] и сортирует по [order]. Навигацию действие несёт
 * С СОБОЙ ([onClick]): чат не знает ни одной фичи.
 */
data class ChatQuickAction(
    /** Уникальный id действия (для diff/аналитики). */
    val id: String,
    /** Подпись на чипе. */
    val title: String,
    /** Порядок в ряду чипов (Set не упорядочен — упорядочиваем сами). */
    val order: Int = 0,
    /** Доступно ли действие пользователю. */
    val isAvailable: () -> Boolean = { true },
    /** Навигация в фичу-владельца; фича обычно дергает свой лаунчер через DI. */
    val onClick: (FragmentActivity) -> Unit,
)
