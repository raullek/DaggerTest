package az.less.core.workflow.api.widget

import az.less.core.workflow.api.model.WorkflowEvent

/**
 * Колбэки из вьюхолдеров в движок: кнопка-событие просит отправить событие/откатиться.
 * Хост реализует это поверх [az.less.core.workflow.api.engine.WorkflowStateMachine]
 * (с клиентской валидацией полей перед отправкой).
 */
interface WorkflowInteraction {

    /** Валидирует поля экрана и, если ок, отправляет [event] на сервер. */
    fun submit(event: WorkflowEvent)

    /** Шаг назад по флоу. */
    fun rollback()
}
