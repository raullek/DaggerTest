package az.less.core.workflow.compose.api.engine

import az.less.core.workflow.compose.api.model.Command
import az.less.core.workflow.compose.api.model.WorkflowRequest
import az.less.core.workflow.compose.api.model.WorkflowResponse

/**
 * Источник экранов: выполняет команду и возвращает замапленный домен-ответ. Реализация
 * композирует транспорт (фейк/Retrofit) + парсер + маппер.
 */
interface WorkflowRepository {
    suspend fun doEvent(
        command: Command,
        flow: String,
        pid: String?,
        eventName: String?,
        request: WorkflowRequest,
    ): WorkflowResponse
}
