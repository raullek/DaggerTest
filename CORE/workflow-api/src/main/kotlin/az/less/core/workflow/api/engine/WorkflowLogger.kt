package az.less.core.workflow.api.engine

import az.less.core.workflow.api.model.Command
import az.less.core.workflow.api.model.WorkflowRequest
import az.less.core.workflow.api.model.WorkflowResponse

/**
 * Точка логирования всех серверных экшенов SDUI. Вызывается в единственной воронке —
 * [WorkflowRepository.doEvent] — на каждый START/EVENT/ROLLBACK, поэтому видит, КАКОЕ событие и
 * С КАКИМИ данными уходит ([WorkflowRequest.fields]) и что вернулось.
 *
 * Дефолт — Android Logcat; можно подменить биндинг на аналитику/файловый логгер, не трогая движок.
 */
interface WorkflowLogger {

    /** Перед отправкой: команда, флоу, имя события и собранные значения полей. */
    fun logAction(command: Command, flow: String, eventName: String?, request: WorkflowRequest)

    /** После ответа: результат и следующий экран. */
    fun logResult(flow: String, response: WorkflowResponse)

    companion object {
        /** Заглушка-нолог. */
        val NONE: WorkflowLogger = object : WorkflowLogger {
            override fun logAction(command: Command, flow: String, eventName: String?, request: WorkflowRequest) = Unit
            override fun logResult(flow: String, response: WorkflowResponse) = Unit
        }
    }
}
