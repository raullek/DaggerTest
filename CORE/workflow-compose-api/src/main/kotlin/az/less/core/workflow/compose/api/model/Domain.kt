package az.less.core.workflow.compose.api.model

/**
 * Доменная модель BDUI-экрана (self-contained, не зависит от XML-движка `:core-workflow`).
 * Иммутабельные POJO (`Screen → Widget → Field → Event/Reference`),
 * рассчитанные на Compose-рендер. Сервер описывает экран JSON'ом, клиент его рисует.
 */

/**
 * Один BDUI-экран с тремя регионами (header/main/footer):
 * - [header] — закреплённая шапка (под степпером/заголовком);
 * - [widgets] — скроллируемое тело;
 * - [footer] — закреплённый подвал (над кнопками-событиями).
 *
 * [strategies] — межвиджетные реактивные связи (см. [StrategyDescriptor]); их собирает
 * StrategyResolver и связывает контроллеры полей по `lookingKey`/`lookUpKey`.
 */
data class WfScreen(
    val title: String,
    val description: String? = null,
    val header: List<WfWidget> = emptyList(),
    val widgets: List<WfWidget> = emptyList(),
    val footer: List<WfWidget> = emptyList(),
    val strategies: List<StrategyDescriptor> = emptyList(),
    val properties: WfProperties = WfProperties.EMPTY,
) {
    /** Все поля экрана (header + body + footer) — для сбора значений и валидации. */
    val fields: List<WfField>
        get() = (header + widgets + footer).flatMap { it.fields }
}

/**
 * Визуальная группа полей. [type] выбирает вьюхолдер-композабл из реестра (Reflector);
 * неизвестный тип откатывается к дефолтному `FIELDSET`.
 */
data class WfWidget(
    val type: String,
    val title: String? = null,
    val description: String? = null,
    val fields: List<WfField> = emptyList(),
    val properties: WfProperties = WfProperties.EMPTY,
)

/**
 * Атомарная единица ввода. [id] — ключ, под которым значение уходит на сервер и по которому
 * стратегии адресуют контроллер. [value] — в server-формате; рендер форматирует его в UI-вид.
 */
data class WfField(
    val id: String,
    val type: FieldType,
    val title: String,
    val value: String = "",
    val description: String? = null,
    val referenceId: String? = null,
    val style: String? = null,
    val readonly: Boolean = false,
    val masked: Boolean = false,
    val visible: Boolean = true,
    val validators: List<FieldValidator> = emptyList(),
    val properties: WfProperties = WfProperties.EMPTY,
)

/** Кнопка-событие экрана. [type] = SUBMIT/ROLLBACK; SUBMIT гоняет клиентскую валидацию. */
data class WfEvent(
    val name: String,
    val title: String,
    val type: EventType = EventType.SUBMIT,
    val hidden: Boolean = false,
) {
    val isRollback: Boolean get() = type == EventType.ROLLBACK
}

enum class EventType { SUBMIT, ROLLBACK }

/** Справочник опций (для SELECT/RADIO), адресуется `field.referenceId`. */
data class WfReference(val id: String, val items: List<WfReferenceItem>)

data class WfReferenceItem(
    val id: String,
    val text: String,
    val description: String? = null,
    val style: String? = null,
)

/** Набор справочников экрана: `referenceId -> WfReference`. */
data class WfReferences(val map: Map<String, WfReference> = emptyMap()) {
    fun items(referenceId: String?): List<WfReferenceItem> =
        referenceId?.let { map[it]?.items }.orEmpty()

    fun item(referenceId: String?, itemId: String?): WfReferenceItem? =
        items(referenceId).firstOrNull { it.id == itemId }

    companion object {
        val EMPTY = WfReferences()
    }
}

/** Серверное сообщение экрана/поля. */
data class WfMessage(
    val type: MessageType,
    val text: String,
    val fieldId: String? = null,
) {
    val isError: Boolean get() = type == MessageType.ERROR || type == MessageType.FATAL
    val isFatal: Boolean get() = type == MessageType.FATAL
}

enum class MessageType { INFO, WARNING, ERROR, FATAL }

/** Нетипизированный бэг свойств с коэрсящими геттерами. */
data class WfProperties(val values: Map<String, String> = emptyMap()) {
    fun string(key: String): String? = values[key]
    fun int(key: String, default: Int = 0): Int = values[key]?.toIntOrNull() ?: default
    fun bool(key: String, default: Boolean = false): Boolean =
        values[key]?.toBooleanStrictOrNull() ?: default

    companion object {
        val EMPTY = WfProperties()
    }
}
