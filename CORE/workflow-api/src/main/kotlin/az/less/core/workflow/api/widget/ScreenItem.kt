package az.less.core.workflow.api.widget

import az.less.core.workflow.api.model.WorkflowEvent
import az.less.core.workflow.api.model.WorkflowReferences
import az.less.core.workflow.api.model.WorkflowWidget

/**
 * Единица рендера экрана в RecyclerView. Экран разворачивается в плоский `List<ScreenItem>`
 * (хедер → степпер? → виджеты → кнопки), каждый со строковым [typeKey], по которому адаптер
 * берёт [WidgetViewHolderFactory] из реестра.
 *
 * Поля заполняются под конкретный тип элемента (для HEADER — [title]/[subtitle], для виджета —
 * [widget]/[references]/[scope], для EVENT — [event], для STEPPER — [step]/[steps]).
 */
class ScreenItem(
    val typeKey: String,
    val widget: WorkflowWidget? = null,
    val event: WorkflowEvent? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val step: Int = 0,
    val steps: Int = 0,
    val references: WorkflowReferences = WorkflowReferences.EMPTY,
    val scope: WidgetScope? = null,
) {
    companion object {
        // Зарезервированные ключи структурных элементов (виджеты используют свой widget.type).
        const val TYPE_HEADER = "HEADER"
        const val TYPE_STEPPER = "STEPPER"
        const val TYPE_EVENT = "EVENT"
    }
}
