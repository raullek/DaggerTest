package az.less.core.workflow.impl.data

import az.less.core.workflow.api.model.Command
import az.less.core.workflow.api.model.WorkflowRequest

/**
 * «Сетевой» порт движка: отдаёт сырой JSON ответа на команду.
 * Здесь реализуется фейком ([FakeWorkflowApi]); подменив
 * биндинг в DI на Retrofit-реализацию (через core-network), получим реальный бэкенд.
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
