package az.less.core.workflow.compose.api.widget

import az.less.core.workflow.compose.api.model.WfEvent
import az.less.core.workflow.compose.api.model.WfReferences
import az.less.core.workflow.compose.api.model.WfWidget

/**
 * Единица рендера экрана: либо виджет ([widget]), либо структурный элемент (header/stepper/event).
 * [typeKey] резолвит композабл из реестра ([Reflector.widgetRenderer]). Несёт [scope] (контроллеры)
 * и [references] для биндинга полей.
 */
class ScreenItem(
    val typeKey: String,
    val scope: WidgetScope,
    val widget: WfWidget? = null,
    val event: WfEvent? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val step: Int = 0,
    val steps: Int = 0,
    val references: WfReferences = WfReferences.EMPTY,
) {
    companion object {
        /** Зарезервированные ключи структурных элементов (не приходят от сервера как widget.type). */
        const val TYPE_HEADER = "__HEADER__"
        const val TYPE_STEPPER = "__STEPPER__"
        const val TYPE_EVENT = "__EVENT__"
    }
}
