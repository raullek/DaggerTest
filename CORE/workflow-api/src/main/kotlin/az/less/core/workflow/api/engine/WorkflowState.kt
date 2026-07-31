package az.less.core.workflow.api.engine

import az.less.core.workflow.api.model.WorkflowResponse

/**
 * Состояние движка для UI: одно sealed-состояние в [StateFlow]
 * (проект на корутинах, без adapter-колбэков).
 */
sealed interface WorkflowState {

    /** Флоу ещё не стартован. */
    data object Idle : WorkflowState

    /** Идёт запрос к серверу. */
    data object Loading : WorkflowState

    /** Пришёл экран — его надо отрисовать. */
    data class Screen(val response: WorkflowResponse) : WorkflowState

    /** Флоу завершён (result == END). */
    data class Finished(val exitUri: String?) : WorkflowState

    /** Сбой (сеть/разбор) — показать ошибку и выйти. */
    data class Failed(val message: String) : WorkflowState
}
