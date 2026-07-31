package az.less.core.workflow.api.model

/** Команда движка серверу (`cmd`: START/EVENT/ROLLBACK/EXIT). */
enum class Command(val wire: String) {
    START("START"),
    EVENT("EVENT"),
    ROLLBACK("ROLLBACK"),
    EXIT("EXIT"),
}

/** Чем обернулся ответ: показать экран, завершить флоу или ошибка разбора. */
enum class WorkflowResult {
    SCREEN,
    END,
    UNKNOWN;

    companion object {
        fun from(raw: String?): WorkflowResult =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}

/**
 * Доменный ответ сервера на команду (после маппинга из DTO).
 * Несёт следующий экран, доступные события, справочники и
 * сообщения (привязанные к полям [fieldMessages] и экранные [messages]).
 */
data class WorkflowResponse(
    val result: WorkflowResult,
    val pid: String? = null,
    val flow: String? = null,
    val state: String? = null,
    val screen: WorkflowScreen? = null,
    val events: List<WorkflowEvent> = emptyList(),
    val references: WorkflowReferences = WorkflowReferences.EMPTY,
    val fieldMessages: Map<String, WorkflowMessage> = emptyMap(),
    val messages: List<WorkflowMessage> = emptyList(),
    /** Куда уйти после END (диплинк/закрытие) — необязательно. */
    val exitUri: String? = null,
) {
    val isEnd: Boolean get() = result == WorkflowResult.END
}
