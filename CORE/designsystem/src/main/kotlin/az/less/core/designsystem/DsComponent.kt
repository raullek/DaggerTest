package az.less.core.designsystem

/**
 * Семантический ключ компонента дизайн-системы. Это «словарь» доступных UI-кирпичей —
 * по нему [DesignSystemInflater] инфлейтит нужный лейаут.
 *
 * Ключ строковый ([from]), поэтому источник (например, server-driven экран через
 * `field.style` или `widget.type`) может выбрать компонент по имени.
 */
enum class DsComponent {
    TEXT_FIELD,
    MONEY_FIELD,
    AMOUNT_FIELD,
    DATE_FIELD,
    SELECT_FIELD,
    CHECKBOX_FIELD,
    RADIO_FIELD,
    SWITCH_FIELD,
    SUMMARY_ROW,
    BANNER,
    BANNER_SUCCESS,
    STEPPER,
    BUTTON,
    BUTTON_SECONDARY,
    SECTION_HEADER,
    CARD;

    companion object {
        fun from(key: String?): DsComponent? =
            entries.firstOrNull { it.name.equals(key, ignoreCase = true) }
    }
}
