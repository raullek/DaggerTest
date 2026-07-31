package az.less.core.workflow.api.check

import az.less.core.workflow.api.model.WorkflowMessage

/**
 * Получатель разложенных валидатором сообщений: один обработчик показывает ошибку,
 * другой — ошибку + завершение флоу.
 */
fun interface WorkflowMessageHandler {
    fun onMessages(messages: List<WorkflowMessage>)
}
