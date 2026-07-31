package az.less.core.workflow.api.check

import az.less.core.workflow.api.model.WorkflowResponse

/**
 * Разбирает ответ сервера на «успех/ошибка/фатал» и роутит сообщения по обработчикам.
 * Два канала: обычные ошибки и фатальные.
 */
interface WorkflowResultValidator {

    /** true — ответ успешный (нет ошибок/фатала). Побочно дёргает обработчики сообщений. */
    fun validate(response: WorkflowResponse): Boolean

    fun setErrorHandler(handler: WorkflowMessageHandler)

    fun setFatalHandler(handler: WorkflowMessageHandler)
}
