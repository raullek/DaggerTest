package az.less.core.workflow.api.model

/**
 * Визуальная группа полей.
 *
 * Виджет не несёт значения сам — он группирует [fields] и говорит рендереру, как их
 * показать ([type], например `FIELDSET`/`CARD`). Выбор рендерера идёт по [type]
 * (см. WidgetRendererFactory).
 */
data class WorkflowWidget(
    val type: String,
    val title: String? = null,
    val description: String? = null,
    val fields: List<WorkflowField> = emptyList(),
    val properties: Map<String, String> = emptyMap(),
)
