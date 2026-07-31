package az.less.core.workflow.compose.impl.check

import az.less.core.workflow.compose.api.check.WorkflowMessageHandler
import az.less.core.workflow.compose.api.check.WorkflowResultValidator
import az.less.core.workflow.compose.api.model.WorkflowResponse
import javax.inject.Inject

/**
 * Разбирает ответ и роутит ошибки по каналам error/fatal (filter→handler).
 * FATAL > ERROR. INFO/WARNING не блокируют переход. Возвращает `false`, если есть блокирующие ошибки
 * → движок остаётся на текущем экране.
 */
class WorkflowResultValidatorImpl @Inject constructor() : WorkflowResultValidator {

    private var errorHandler: WorkflowMessageHandler? = null
    private var fatalHandler: WorkflowMessageHandler? = null

    override fun setErrorHandler(handler: WorkflowMessageHandler) {
        errorHandler = handler
    }

    override fun setFatalHandler(handler: WorkflowMessageHandler) {
        fatalHandler = handler
    }

    override fun validate(response: WorkflowResponse): Boolean {
        val fatals = response.messages.filter { it.isFatal }
        val errors = response.messages.filter { it.isError && !it.isFatal }

        if (fatals.isNotEmpty()) {
            fatalHandler?.onMessages(fatals)
            return false
        }
        if (errors.isNotEmpty()) {
            errorHandler?.onMessages(errors)
            return false
        }
        return true
    }
}
