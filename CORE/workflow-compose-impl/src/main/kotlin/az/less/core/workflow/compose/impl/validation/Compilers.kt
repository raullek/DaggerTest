package az.less.core.workflow.compose.impl.validation

import az.less.core.workflow.compose.api.model.FieldType
import az.less.core.workflow.compose.api.model.FieldValidator
import az.less.core.workflow.compose.api.validation.ValidatorCompiler
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Компиляторы валидаторов (`validator.type -> ValidatorCompiler`). Числовые применяются только к
 * числовым полям (double-dispatch тип-валидатора × тип-поля).
 */

class RequiredCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String?, message: String, fieldType: FieldType) =
        FieldValidator.Required(message)
}

class MinLengthCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String?, message: String, fieldType: FieldType): FieldValidator? =
        rawValue?.toIntOrNull()?.let { FieldValidator.MinLength(it, message) }
}

class MaxLengthCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String?, message: String, fieldType: FieldType): FieldValidator? =
        rawValue?.toIntOrNull()?.let { FieldValidator.MaxLength(it, message) }
}

class RegexpCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String?, message: String, fieldType: FieldType): FieldValidator? =
        rawValue?.takeIf { it.isNotEmpty() }?.let { FieldValidator.Regexp(it, message) }
}

class MinValueCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String?, message: String, fieldType: FieldType): FieldValidator? {
        if (!fieldType.isNumeric) return null
        return rawValue?.toBigDecimalOrNull()?.let { FieldValidator.MinValue(it, message) }
    }
}

class MaxValueCompiler @Inject constructor() : ValidatorCompiler {
    override fun compile(rawValue: String?, message: String, fieldType: FieldType): FieldValidator? {
        if (!fieldType.isNumeric) return null
        return rawValue?.toBigDecimalOrNull()?.let { FieldValidator.MaxValue(it, message) }
    }
}

private fun String.toBigDecimalOrNull(): BigDecimal? = try {
    BigDecimal(this)
} catch (_: NumberFormatException) {
    null
}
