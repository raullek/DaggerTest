package az.less.core.workflow.compose.api.check

import az.less.core.workflow.compose.api.model.WfMessage
import az.less.core.workflow.compose.api.model.WorkflowResponse

/** Приёмник пачки сообщений одного канала. */
fun interface WorkflowMessageHandler {
    fun onMessages(messages: List<WfMessage>)
}

/**
 * Разбирает ответ и роутит сообщения по каналам error/fatal (filter→handler).
 * Возвращает `true`, если можно двигаться дальше (нет блокирующих ошибок).
 */
interface WorkflowResultValidator {
    fun setErrorHandler(handler: WorkflowMessageHandler)
    fun setFatalHandler(handler: WorkflowMessageHandler)
    fun validate(response: WorkflowResponse): Boolean
}
