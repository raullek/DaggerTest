package az.less.core.workflow.compose.api.model

/**
 * Тип поля BDUI-экрана. Сервер шлёт строку, [from] приводит её к известному типу.
 * От типа зависит: какой field-рендерер строит UI, какие валидаторы компилируются и какие
 * форматтеры применяются (server ↔ ui).
 */
enum class FieldType {
    TEXT,
    INTEGER,
    DECIMAL,
    MONEY,
    DATE,
    PHONE,
    SELECT,
    CHECKBOX,
    RADIO,
    SWITCH,

    /** Неизвестный серверу тип — рендерится как read-only заглушка. */
    UNKNOWN;

    val isNumeric: Boolean get() = this == INTEGER || this == DECIMAL || this == MONEY

    companion object {
        fun from(raw: String?): FieldType =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}
