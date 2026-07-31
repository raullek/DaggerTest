package az.less.core.workflow.compose.impl.data

import az.less.core.workflow.compose.api.model.Command
import az.less.core.workflow.compose.api.model.WorkflowRequest

/**
 * Низкоуровневый транспорт BDUI: команда → JSON-строка экрана. Подменяется на Retrofit-реализацию
 * без правок движка/рендера.
 */
interface WorkflowApi {
    suspend fun doEvent(
        command: Command,
        flow: String,
        pid: String?,
        eventName: String?,
        request: WorkflowRequest,
    ): String
}
