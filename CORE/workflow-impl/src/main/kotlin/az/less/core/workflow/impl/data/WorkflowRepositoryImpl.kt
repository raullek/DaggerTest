package az.less.core.workflow.impl.data

import az.less.core.workflow.api.engine.WorkflowLogger
import az.less.core.workflow.api.engine.WorkflowRepository
import az.less.core.workflow.api.model.Command
import az.less.core.workflow.api.model.WorkflowRequest
import az.less.core.workflow.api.model.WorkflowResponse
import az.less.core.workflow.impl.dto.WorkflowJsonParser
import az.less.core.workflow.impl.mapper.WorkflowResponseMapper
import javax.inject.Inject

/**
 * Реализация [WorkflowRepository]: «сеть» ([WorkflowApi]) → JSON → DTO ([WorkflowJsonParser]) →
 * домен ([WorkflowResponseMapper]). Единственная воронка всех экшенов — здесь же логируем, какое
 * событие и с какими данными уходит ([WorkflowLogger]).
 */
class WorkflowRepositoryImpl @Inject constructor(
    private val api: WorkflowApi,
    private val mapper: WorkflowResponseMapper,
    private val logger: WorkflowLogger,
) : WorkflowRepository {

    override suspend fun doEvent(
        command: Command,
        flow: String,
        pid: String?,
        eventName: String?,
        request: WorkflowRequest,
    ): WorkflowResponse {
        logger.logAction(command, flow, eventName, request)
        val json = api.doEvent(command, flow, pid, eventName, request)
        val response = mapper.map(WorkflowJsonParser.parse(json))
        logger.logResult(flow, response)
        return response
    }
}
