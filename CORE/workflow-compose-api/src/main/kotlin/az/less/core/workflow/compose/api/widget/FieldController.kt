package az.less.core.workflow.compose.api.widget

import az.less.core.workflow.compose.api.format.FormatterRegistry
import az.less.core.workflow.compose.api.model.FieldType
import az.less.core.workflow.compose.api.model.WfField
import az.less.core.workflow.compose.api.model.WfReferences
import az.less.core.workflow.compose.api.protocol.ErrorProtocol
import az.less.core.workflow.compose.api.protocol.MutableReadonlyProtocol
import az.less.core.workflow.compose.api.protocol.MutableValueProtocol
import az.less.core.workflow.compose.api.protocol.MutableVisibilityProtocol
import az.less.core.workflow.compose.api.protocol.TitleProtocol

/**
 * Контроллер одного поля. Держит состояние поля в
 * Compose-`State` (источник истины), реализует базовые протоколы (value/error/visibility/readonly/
 * title), а специфичные (References/Description/Style) добавляют конкретные контроллеры.
 *
 * Два канала значения против echo-петель:
 * - [onUiInput] — ввод пользователя: обновляет состояние и **уведомляет** слушателей (драйвит
 *   стратегии);
 * - [MutableValueProtocol.setValueFromModel] — программно/из стратегии: обновляет состояние, но
 *   **не** уведомляет слушателей (стратегии не зацикливаются).
 *
 * Свойства-геттеры (`value`/`error`/`visible`/`readonly`) читают Compose-State — вызов в `@Composable`
 * автоматически реактивен.
 */
interface FieldController :
    MutableValueProtocol,
    ErrorProtocol,
    MutableVisibilityProtocol,
    MutableReadonlyProtocol,
    TitleProtocol {

    val field: WfField

    val key: String get() = this.field.id

    /** Ввод пользователя (UI-канал). */
    fun onUiInput(value: String)

    /** Значение в человекочитаемом UI-виде (применён ui-форматтер) — для readonly/summary. */
    fun displayValue(): String

    /** Прогнать клиентскую валидацию, выставить/снять ошибку. Возвращает валидность. */
    fun validate(): Boolean

    /** Собрать значение для запроса в server-формате (применяет форматтер). */
    fun collect(): String

    /** Подписка StrategyResolver на пользовательские изменения значения. */
    fun observeChanges(listener: () -> Unit)
}

/**
 * Фабрика контроллера для конкретного [FieldType]. Регистрируется в реестр
 * `Map<field.type, FieldControllerFactory>`; неизвестный тип → дефолтная (read-only) фабрика.
 */
fun interface FieldControllerFactory {
    fun create(
        field: WfField,
        references: WfReferences,
        formatters: FormatterRegistry,
    ): FieldController
}
