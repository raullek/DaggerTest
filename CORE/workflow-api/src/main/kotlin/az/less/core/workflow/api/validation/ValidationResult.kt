package az.less.core.workflow.api.validation

/**
 * Итог проверки значения поля.
 * [error] != null означает провал и текст под полем.
 */
data class ValidationResult(
    val valid: Boolean,
    val error: String? = null,
) {
    companion object {
        val VALID = ValidationResult(valid = true)
        fun invalid(message: String) = ValidationResult(valid = false, error = message)
    }
}
