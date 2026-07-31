package az.less.core.workflow.api.engine

import az.less.core.workflow.api.model.Command
import az.less.core.workflow.api.model.WorkflowRequest
import az.less.core.workflow.api.model.WorkflowResponse

/**
 * Источник экранов. За интерфейсом может стоять реальный Retrofit
 * либо фейковый in-memory сервер (в этой сборке — фейк).
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
