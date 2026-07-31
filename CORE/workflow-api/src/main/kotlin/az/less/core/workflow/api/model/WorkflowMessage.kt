package az.less.core.workflow.api.model

/**
 * Сообщение сервера (ошибка/инфо).
 *
 * Делятся на привязанные к полю (по [code] == fieldId, показываются под полем) и
 * экранные (показываются как диалог/тост). Тип FATAL завершает флоу.
 */
data class WorkflowMessage(
    val text: String,
    val type: Type = Type.INFO,
    val code: String? = null,
) {
    enum class Type { INFO, ERROR, FATAL }

    val isError: Boolean get() = type == Type.ERROR || type == Type.FATAL
}
