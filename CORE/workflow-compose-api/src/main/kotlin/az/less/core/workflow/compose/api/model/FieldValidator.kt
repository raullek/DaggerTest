package az.less.core.workflow.compose.api.model

import java.math.BigDecimal

/**
 * Скомпилированный клиентский валидатор поля. Сервер присылает сырой `{type, value, message}`,
 * а ResponseMapper превращает его в типизированного наследника по [FieldType] поля
 * (double-dispatch: тип-валидатора × тип-поля).
 */
sealed interface FieldValidator {
    val message: String

    data class Required(override val message: String) : FieldValidator
    data class MinLength(val min: Int, override val message: String) : FieldValidator
    data class MaxLength(val max: Int, override val message: String) : FieldValidator
    data class Regexp(val pattern: String, override val message: String) : FieldValidator
    data class MinValue(val min: BigDecimal, override val message: String) : FieldValidator
    data class MaxValue(val max: BigDecimal, override val message: String) : FieldValidator
}
