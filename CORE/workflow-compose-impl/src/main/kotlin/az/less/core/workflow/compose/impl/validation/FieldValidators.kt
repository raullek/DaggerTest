package az.less.core.workflow.compose.impl.validation

import az.less.core.workflow.compose.api.model.FieldValidator
import az.less.core.workflow.compose.api.validation.ValidationResult
import java.math.BigDecimal

/**
 * Исполнитель клиентской валидации: гоняет правила по порядку, возвращает первый провал.
 * Значение — в «натуральном» виде поля.
 */
object FieldValidators {

    fun validate(validators: List<FieldValidator>, value: String): ValidationResult {
        for (validator in validators) {
            val result = check(validator, value)
            if (!result.valid) return result
        }
        return ValidationResult.VALID
    }

    private fun check(validator: FieldValidator, value: String): ValidationResult = when (validator) {
        is FieldValidator.Required ->
            if (value.isBlank()) ValidationResult.invalid(validator.message) else ValidationResult.VALID

        is FieldValidator.MinLength ->
            if (value.length < validator.min) ValidationResult.invalid(validator.message) else ValidationResult.VALID

        is FieldValidator.MaxLength ->
            if (value.length > validator.max) ValidationResult.invalid(validator.message) else ValidationResult.VALID

        is FieldValidator.Regexp ->
            if (value.isNotEmpty() && !Regex(validator.pattern).matches(value)) {
                ValidationResult.invalid(validator.message)
            } else {
                ValidationResult.VALID
            }

        is FieldValidator.MinValue -> {
            val number = value.toBigDecimalOrNull()
            if (number != null && number < validator.min) {
                ValidationResult.invalid(validator.message)
            } else {
                ValidationResult.VALID
            }
        }

        is FieldValidator.MaxValue -> {
            val number = value.toBigDecimalOrNull()
            if (number != null && number > validator.max) {
                ValidationResult.invalid(validator.message)
            } else {
                ValidationResult.VALID
            }
        }
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? = try {
        BigDecimal(replace(",", ".").replace(" ", "").replace("₽", "").trim())
    } catch (_: NumberFormatException) {
        null
    }
}
