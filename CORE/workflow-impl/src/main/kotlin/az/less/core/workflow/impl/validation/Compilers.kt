package az.less.core.workflow.impl.validation

import az.less.core.workflow.api.model.FieldType
import az.less.core.workflow.api.model.FieldValidator
import az.less.core.workflow.api.validation.ValidatorCompiler
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Компиляторы типов серверных валидаторов в типизированные [FieldValidator]. Регистрируются
 * `@IntoMap` по типу валидатора (см. WorkflowValidatorsModule) — фича может добавить свой тип.
 * Разнесены по классам ради расширяемости.
 */

class RequiredCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String, message: String, fieldType: FieldType): FieldValidator =
        FieldValidator.Required(message)
}

class MinLengthCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String, message: String, fieldType: FieldType): FieldValidator? =
        rawValue.toIntOrNull()?.let { FieldValidator.MinLength(it, message) }
}

class MaxLengthCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String, message: String, fieldType: FieldType): FieldValidator? =
        rawValue.toIntOrNull()?.let { FieldValidator.MaxLength(it, message) }
}

class RegexpCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String, message: String, fieldType: FieldType): FieldValidator =
        FieldValidator.Regexp(rawValue, message)
}

class MinValueCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String, message: String, fieldType: FieldType): FieldValidator? =
        if (fieldType.isNumeric()) rawValue.toBigDecimalOrNull()?.let { FieldValidator.MinValue(it, message) } else null
}

class MaxValueCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String, message: String, fieldType: FieldType): FieldValidator? =
        if (fieldType.isNumeric()) rawValue.toBigDecimalOrNull()?.let { FieldValidator.MaxValue(it, message) } else null
}

private fun FieldType.isNumeric(): Boolean =
    this == FieldType.INTEGER || this == FieldType.DECIMAL || this == FieldType.MONEY

private fun String.toBigDecimalOrNull(): BigDecimal? =
    try { BigDecimal(trim()) } catch (e: NumberFormatException) { null }
