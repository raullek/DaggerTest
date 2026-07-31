package az.less.core.workflow.api.model

/**
 * Действие на экране (кнопка «Далее»/«Назад»/«Отправить»).
 *
 * При нажатии клиент собирает значения полей и шлёт [name] на сервер — тот отвечает
 * следующим экраном (или END). [hidden] событие не рисует кнопку (авто-/форм-триггер).
 */
data class WorkflowEvent(
    val name: String,
    val title: String? = null,
    val type: String? = null,
    val hidden: Boolean = false,
) {
    /** Кнопка «назад» — откатывает флоу, а не отправляет данные (по type/name). */
    val isRollback: Boolean
        get() = type.equals("ROLLBACK", true) || name.equals("BACK", true)
}
