package az.less.core.workflow.api.widget

import az.less.core.workflow.api.model.WorkflowField

/**
 * Память значений и ошибок полей одного экрана: рендереры
 * пишут пользовательский ввод сюда, а при отправке события движок забирает всё через
 * [retrieveData].
 *
 * Создаётся заново на каждый экран (живёт ровно столько, сколько показанный экран).
 */
interface WidgetScope {

    /** Наблюдаемое значение поля (server-формат). Рендерер читает/пишет его. */
    fun valueHolder(field: WorkflowField): ObservableValue<String>

    /** Наблюдаемый текст ошибки поля (null — ошибки нет). */
    fun errorHolder(field: WorkflowField): ObservableValue<String?>

    /** Собрать `fieldId -> value` по всем не-readonly полям для отправки на сервер. */
    fun retrieveData(): Map<String, String>
}
