package az.less.core.workflow.compose.api.format

import az.less.core.workflow.compose.api.model.FieldType

/** Server-значение → отображаемая UI-строка. */
fun interface UiValueFormatter {
    fun toUi(serverValue: String): String
}

/** UI-ввод → server-строка. */
fun interface ServerValueFormatter {
    fun toServer(uiValue: String): String
}

/** Симметричная пара форматтеров одного типа поля. */
data class ValueFormatter(
    val ui: UiValueFormatter,
    val server: ServerValueFormatter,
) {
    companion object {
        /** Тождество — значение не меняется в обе стороны. */
        val IDENTITY = ValueFormatter(
            ui = UiValueFormatter { it },
            server = ServerValueFormatter { it },
        )
    }
}

/** Реестр форматтеров по типу поля. Неизвестный тип → [ValueFormatter.IDENTITY]. */
interface FormatterRegistry {
    fun formatterFor(type: FieldType): ValueFormatter
}
