package az.less.core.workflow.compose.api.engine

import az.less.core.workflow.compose.api.model.WorkflowRequest
import az.less.core.workflow.compose.api.model.WorkflowResponse
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Движок выполнения BDUI-флоу. Короткоживущий — один инстанс на запуск флоу
 * (см. `WorkflowComposeFeatureApi.newStateMachine()`). Построен на корутинах/Flow.
 *
 * Цикл: [start]/[sendEvent] → Loading → запрос → разбор → новое [state]. Ошибки/инфо уходят в
 * [messages]. [rollback] — локальный шаг назад по истории экранов.
 */
interface BduiStateMachine {

    val state: StateFlow<WorkflowState>

    val messages: SharedFlow<WorkflowUserMessage>

    val currentResponse: WorkflowResponse?

    fun start(flow: String, request: WorkflowRequest? = null)

    fun sendEvent(eventName: String, fields: Map<String, String>, force: Boolean = false)

    fun rollback()

    fun reset()
}
