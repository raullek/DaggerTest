package az.less.core.workflow.api.format

import az.less.core.workflow.api.model.FieldType

/**
 * Реестр форматтеров по [FieldType].
 * Рендереры берут отсюда пару (ui/server) для конкретного поля.
 */
interface FormatterRegistry {

    /** Форматтер для типа поля; для неизвестных — [ValueFormatter.IDENTITY]. */
    fun formatterFor(type: FieldType): ValueFormatter
}
