package az.less.core.workflow.api.model

/**
 * Тип поля server-driven экрана: сервер шлёт строку, [from] приводит её к известному
 * enum-типу.
 *
 * От типа зависит: какой рендерер строит UI, какие валидаторы компилируются
 * (см. [FieldValidator]) и какие форматтеры применяются (server ↔ ui).
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

    companion object {
        fun from(raw: String?): FieldType =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}
