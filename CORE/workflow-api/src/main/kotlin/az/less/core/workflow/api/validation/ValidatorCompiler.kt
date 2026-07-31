package az.less.core.workflow.api.validation

import az.less.core.workflow.api.model.FieldType
import az.less.core.workflow.api.model.FieldValidator

/**
 * Компилятор одного типа серверного валидатора (`REQUIRED`/`REGEXP`/`MIN_VALUE`/…) в
 * типизированный [FieldValidator] с учётом типа поля. Компиляторы собраны
 * в реестр `Map<String, ValidatorCompiler>` (`@IntoMap` по типу валидатора) —
 * фича может добавить свой тип валидатора, не трогая ядро.
 */
interface ValidatorCompiler {

    /** Скомпилировать или вернуть null, если значение/тип поля несовместимы. */
    fun compile(rawValue: String, message: String, fieldType: FieldType): FieldValidator?
}
