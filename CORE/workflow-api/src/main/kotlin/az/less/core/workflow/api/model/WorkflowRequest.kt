package az.less.core.workflow.api.model

/**
 * Системные атрибуты документа, которые клиент эхом возвращает серверу для синхронизации
 * состояния флоу.
 */
data class DocumentAttributes(
    val flow: String? = null,
    val state: String? = null,
    val documentId: String? = null,
    val additional: Map<String, String> = emptyMap(),
)

/**
 * Тело запроса на продвижение флоу: системные [document] атрибуты + бизнес-значения
 * полей [fields] (`fieldId -> value`).
 */
data class WorkflowRequest(
    val document: DocumentAttributes,
    val fields: Map<String, String> = emptyMap(),
)
