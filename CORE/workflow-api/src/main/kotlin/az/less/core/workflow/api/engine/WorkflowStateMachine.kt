package az.less.core.workflow.api.engine

import az.less.core.workflow.api.model.WorkflowRequest
import az.less.core.workflow.api.model.WorkflowResponse
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/** Сообщение для пользователя, выработанное движком/валидатором (тост/диалог). */
data class WorkflowUserMessage(val text: String, val fatal: Boolean)

/**
 * Сердце движка: гоняет цикл «событие → запрос → ответ → новое состояние» на корутинах:
 *
 * - [state] — текущее состояние экрана ([WorkflowState]); UI подписывается на него.
 * - [messages] — поток сообщений сервера (ошибки), разложенных валидатором.
 * - [start] — стартовое событие (cmd=START), [sendEvent] — продвижение (cmd=EVENT),
 *   [rollback] — шаг назад по локальной истории.
 *
 * Создаётся на каждый запуск флоу (короткоживущий), поэтому держит собственное
 * состояние и историю.
 */
interface WorkflowStateMachine {

    val state: StateFlow<WorkflowState>

    val messages: SharedFlow<WorkflowUserMessage>

    val currentResponse: WorkflowResponse?

    fun start(flow: String, request: WorkflowRequest? = null)

    /** Отправить событие [eventName] с собранными значениями полей [fields]. */
    fun sendEvent(eventName: String, fields: Map<String, String>, force: Boolean = false)

    /** Откатиться на предыдущий экран (если есть). */
    fun rollback()

    /** Сбросить состояние и отменить активные запросы. */
    fun reset()
}
