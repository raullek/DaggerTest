package az.less.core.workflow.compose.impl.data

import az.less.core.workflow.compose.api.engine.WorkflowLogger
import az.less.core.workflow.compose.api.engine.WorkflowRepository
import az.less.core.workflow.compose.api.model.Command
import az.less.core.workflow.compose.api.model.WorkflowRequest
import az.less.core.workflow.compose.api.model.WorkflowResponse
import az.less.core.workflow.compose.impl.dto.JsonParser
import az.less.core.workflow.compose.impl.mapper.ResponseMapper
import javax.inject.Inject

/**
 * Композирует транспорт ([WorkflowApi]) + парсер + маппер и логирует экшен.
 */
class WorkflowRepositoryImpl @Inject constructor(
    private val api: WorkflowApi,
    private val mapper: ResponseMapper,
    private val logger: WorkflowLogger,
) : WorkflowRepository {

    override suspend fun doEvent(
        command: Command,
        flow: String,
        pid: String?,
        eventName: String?,
        request: WorkflowRequest,
    ): WorkflowResponse {
        logger.logEvent(
            "command=$command flow=$flow state=${request.document.state} event=$eventName " +
                "fields=${request.fields.keys}",
        )
        val json = api.doEvent(command, flow, pid, eventName, request)
        val response = mapper.map(JsonParser.parse(json))
        logger.logEvent("→ result=${response.result} state=${response.state}")
        return response
    }
}
