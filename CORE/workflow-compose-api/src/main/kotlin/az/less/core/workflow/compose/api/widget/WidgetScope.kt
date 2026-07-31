package az.less.core.workflow.compose.api.widget

/**
 * Реестр контроллеров полей одного экрана. Создаётся на каждый экран,
 * держит контроллеры по `field.id`, агрегирует сбор значений и валидацию. По нему же StrategyResolver
 * находит `looking`/`lookUp` контроллеры.
 */
interface WidgetScope {
    fun controller(fieldId: String): FieldController?
    fun controllers(): List<FieldController>

    /** Собрать значения всех видимых полей в server-формате (для запроса). */
    fun retrieveData(): Map<String, String>

    /** Прогнать валидацию всех видимых полей; вернуть `false` при первом провале (с подсветкой). */
    fun validateAll(): Boolean
}
