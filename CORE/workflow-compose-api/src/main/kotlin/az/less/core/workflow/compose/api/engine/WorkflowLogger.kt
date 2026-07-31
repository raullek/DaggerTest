package az.less.core.workflow.compose.api.engine

/**
 * Логгер экшенов BDUI. Подмени биндинг, чтобы слать в аналитику вместо Logcat.
 */
interface WorkflowLogger {
    fun logEvent(message: String)
}
