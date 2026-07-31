package az.less.core.workflow.api.model

import java.math.BigDecimal

/**
 * Скомпилированный валидатор поля: сервер присылает
 * сырой `{type, value, message}`, а маппер ([модуль impl]) превращает его в один из
 * типизированных наследников в зависимости от [FieldType] поля.
 *
 * Запускаются перед отправкой события (onComplete) и/или на изменение значения.
 */
sealed interface FieldValidator {

    /** Текст ошибки, который показывается пользователю при провале. */
    val message: String

    /** Значение не должно быть пустым. */
    data class Required(override val message: String) : FieldValidator

    /** Минимальная длина строки. */
    data class MinLength(val min: Int, override val message: String) : FieldValidator

    /** Максимальная длина строки. */
    data class MaxLength(val max: Int, override val message: String) : FieldValidator

    /** Значение должно матчиться регуляркой. */
    data class Regexp(val pattern: String, override val message: String) : FieldValidator

    /** Минимальное числовое значение (INTEGER/DECIMAL/MONEY). */
    data class MinValue(val min: BigDecimal, override val message: String) : FieldValidator

    /** Максимальное числовое значение (INTEGER/DECIMAL/MONEY). */
    data class MaxValue(val max: BigDecimal, override val message: String) : FieldValidator
}
