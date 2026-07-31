package az.less.core.workflow.compose.api.model

/** Команда к «серверу». */
enum class Command { START, EVENT, ROLLBACK, EXIT }

/** Результат ответа сервера: показать экран или завершить флоу. */
enum class WorkflowResult { SCREEN, END, UNKNOWN }

/**
 * Запрос клиента: системные атрибуты документа + значения полей (server-формат).
 */
data class WorkflowRequest(
    val document: DocumentAttributes,
    val fields: Map<String, String> = emptyMap(),
)

/** Атрибуты сессии флоу — эхом возвращаются серверу для синхронизации состояния. */
data class DocumentAttributes(
    val flow: String,
    val state: String? = null,
    val documentId: String? = null,
)

/**
 * Замапленный ответ сервера (домен). Несёт экран, события, справочники и сообщения.
 */
data class WorkflowResponse(
    val result: WorkflowResult,
    val pid: String?,
    val flow: String,
    val state: String,
    val screen: WfScreen?,
    val events: List<WfEvent> = emptyList(),
    val references: WfReferences = WfReferences.EMPTY,
    val messages: List<WfMessage> = emptyList(),
    val exitUri: String? = null,
) {
    val isEnd: Boolean get() = result == WorkflowResult.END
}
