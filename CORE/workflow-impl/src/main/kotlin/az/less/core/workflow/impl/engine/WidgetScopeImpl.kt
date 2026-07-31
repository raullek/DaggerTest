package az.less.core.workflow.impl.engine

import az.less.core.workflow.api.model.WorkflowField
import az.less.core.workflow.api.model.WorkflowScreen
import az.less.core.workflow.api.widget.ObservableValue
import az.less.core.workflow.api.widget.WidgetScope

/**
 * Память значений/ошибок полей одного экрана. Создаётся
 * заново на каждый показанный экран; [retrieveData] собирает ввод для отправки.
 */
class WidgetScopeImpl(screen: WorkflowScreen) : WidgetScope {

    private val fields: List<WorkflowField> = screen.fields
    private val values = HashMap<String, ObservableValue<String>>()
    private val errors = HashMap<String, ObservableValue<String?>>()

    init {
        fields.forEach { field ->
            values[field.id] = ObservableValue(field.value)
            errors[field.id] = ObservableValue(null)
        }
    }

    override fun valueHolder(field: WorkflowField): ObservableValue<String> =
        values.getOrPut(field.id) { ObservableValue(field.value) }

    override fun errorHolder(field: WorkflowField): ObservableValue<String?> =
        errors.getOrPut(field.id) { ObservableValue(null) }

    override fun retrieveData(): Map<String, String> =
        fields.filterNot { it.readonly }
            .associate { it.id to (values[it.id]?.get().orEmpty()) }
}
