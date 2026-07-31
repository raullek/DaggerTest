package az.less.core.workflow.compose.api.engine

import az.less.core.workflow.compose.api.model.WorkflowResponse

/**
 * Состояние BDUI-движка (LOADING/SCREEN плюс терминальные ветки). Compose-хост
 * подписывается на него через `StateFlow` и рендерит соответствующий экран.
 */
sealed interface WorkflowState {
    /** Движок создан, флоу ещё не стартовал. */
    data object Idle : WorkflowState

    /** Идёт запрос к серверу. */
    data object Loading : WorkflowState

    /** Показать экран из ответа. */
    data class Screen(val response: WorkflowResponse) : WorkflowState

    /** Флоу завершён (result=END). */
    data class Finished(val exitUri: String?) : WorkflowState

    /** Непредвиденная ошибка обработки. */
    data class Failed(val message: String) : WorkflowState
}

/** Сообщение пользователю (тост/снэк). [fatal] закрывает флоу. */
data class WorkflowUserMessage(val text: String, val fatal: Boolean = false)
