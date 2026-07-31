package az.less.core.workflow.compose.api.validation

import az.less.core.workflow.compose.api.model.FieldType
import az.less.core.workflow.compose.api.model.FieldValidator

/** Результат клиентской валидации одного поля. */
data class ValidationResult(val valid: Boolean, val error: String? = null) {
    companion object {
        val VALID = ValidationResult(true)
        fun invalid(message: String) = ValidationResult(false, message)
    }
}

/**
 * Компилятор одного типа валидатора: сырой `{type, value, message}` + тип поля → типизированный
 * [FieldValidator] (или null, если тип-валидатора неприменим к этому типу поля). Регистрируется в
 * реестр `Map<validatorType, ValidatorCompiler>` через мультибиндинги.
 */
interface ValidatorCompiler {
    fun compile(rawValue: String?, message: String, fieldType: FieldType): FieldValidator?
}
