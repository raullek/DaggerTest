package az.less.core.workflow.impl.check

import az.less.core.workflow.api.check.WorkflowMessageHandler
import az.less.core.workflow.api.check.WorkflowResultValidator
import az.less.core.workflow.api.model.WorkflowMessage
import az.less.core.workflow.api.model.WorkflowResponse
import javax.inject.Inject

/**
 * Разбор ответа на успех/ошибка/фатал и маршрутизация сообщений по двум каналам:
 *
 * - сообщения типа ERROR → [errorHandler] (показать, остаться на экране);
 * - сообщения типа FATAL → [fatalHandler] (показать и завершить флоу).
 *
 * Сообщения, привязанные к полям, и экранные обрабатываются одинаково.
 */
class WorkflowResultValidatorImpl @Inject constructor() : WorkflowResultValidator {

    private var errorHandler: WorkflowMessageHandler = WorkflowMessageHandler { }
    private var fatalHandler: WorkflowMessageHandler = WorkflowMessageHandler { }

    override fun setErrorHandler(handler: WorkflowMessageHandler) {
        errorHandler = handler
    }

    override fun setFatalHandler(handler: WorkflowMessageHandler) {
        fatalHandler = handler
    }

    override fun validate(response: WorkflowResponse): Boolean {
        val all: List<WorkflowMessage> = response.messages + response.fieldMessages.values

        val fatal = all.filter { it.type == WorkflowMessage.Type.FATAL }
        val errors = all.filter { it.type == WorkflowMessage.Type.ERROR }

        if (errors.isNotEmpty()) errorHandler.onMessages(errors)
        if (fatal.isNotEmpty()) fatalHandler.onMessages(fatal)

        return fatal.isEmpty() && errors.isEmpty()
    }
}
