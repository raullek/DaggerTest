package az.less.core.workflow.compose.api.widget

import az.less.core.workflow.compose.api.model.WfEvent

/**
 * Канал действий из UI в движок. Кнопки-события дёргают его: SUBMIT гоняет валидацию и шлёт
 * событие, ROLLBACK — локальный шаг назад.
 */
interface WorkflowInteraction {
    fun submit(event: WfEvent)
    fun rollback()
}
