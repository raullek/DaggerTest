package az.less.core.workflow.impl.validation

import az.less.core.workflow.api.model.FieldValidator
import az.less.core.workflow.api.validation.ValidationResult
import java.math.BigDecimal

/**
 * Прогон скомпилированных валидаторов поля по значению: последовательно проверяем правила, первый
 * провал — результат. Числовые правила игнорируют непарсящееся значение (это поймает
 * Required/Regexp).
 */
object FieldValidators {

    /** Проверить [value] по списку правил. Первый провал возвращается как ошибка. */
    fun validate(validators: List<FieldValidator>, value: String): ValidationResult {
        for (validator in validators) {
            val failed = when (validator) {
                is FieldValidator.Required -> value.isBlank()
                is FieldValidator.MinLength -> value.length < validator.min
                is FieldValidator.MaxLength -> value.length > validator.max
                is FieldValidator.Regexp -> !value.matches(validator.pattern.toRegex())
                is FieldValidator.MinValue -> value.toBigDecimalOrNull()
                    ?.let { it < validator.min } ?: false
                is FieldValidator.MaxValue -> value.toBigDecimalOrNull()
                    ?.let { it > validator.max } ?: false
            }
            if (failed) return ValidationResult.invalid(validator.message)
        }
        return ValidationResult.VALID
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? = try {
        BigDecimal(this.trim())
    } catch (e: NumberFormatException) {
        null
    }
}
