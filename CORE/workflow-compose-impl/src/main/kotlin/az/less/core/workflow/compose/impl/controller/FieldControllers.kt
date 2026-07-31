package az.less.core.workflow.compose.impl.controller

import androidx.compose.runtime.mutableStateOf
import az.less.core.workflow.compose.api.format.FormatterRegistry
import az.less.core.workflow.compose.api.model.WfField
import az.less.core.workflow.compose.api.model.WfReferenceItem
import az.less.core.workflow.compose.api.model.WfReferences
import az.less.core.workflow.compose.api.protocol.MutableDescriptionProtocol
import az.less.core.workflow.compose.api.protocol.MutableStyleProtocol
import az.less.core.workflow.compose.api.protocol.ReferencesProtocol
import az.less.core.workflow.compose.api.widget.FieldController
import az.less.core.workflow.compose.impl.validation.FieldValidators

/**
 * Базовый контроллер поля на Compose-`State`. Источник истины — [valueState] (натуральное значение
 * поля: текст/id/«true»). Реализует все мутабельные базовые протоколы + description/style, поэтому
 * любое поле может быть `lookUp`-целью стратегий.
 *
 * Echo-защита: [onUiInput] уведомляет слушателей (драйвит
 * стратегии), [setValueFromModel] — нет (стратегии не зацикливаются).
 */
open class DefaultFieldController(
    final override val field: WfField,
    private val formatters: FormatterRegistry,
) : FieldController, MutableDescriptionProtocol, MutableStyleProtocol {

    private val valueState = mutableStateOf(field.value)
    private val errorState = mutableStateOf<String?>(null)
    private val visibleState = mutableStateOf(field.visible)
    private val readonlyState = mutableStateOf(field.readonly)
    private val descriptionState = mutableStateOf(field.description)
    private val styleState = mutableStateOf(field.style)

    private val changeListeners = mutableListOf<() -> Unit>()

    override val title: String get() = this.field.title

    override val value: String get() = valueState.value
    override val error: String? get() = errorState.value
    override val visible: Boolean get() = visibleState.value
    override val readonly: Boolean get() = readonlyState.value
    override val description: String? get() = descriptionState.value
    override val style: String? get() = styleState.value

    override fun onUiInput(value: String) {
        valueState.value = value
        if (errorState.value != null) errorState.value = null
        changeListeners.toList().forEach { it() }
    }

    override fun setValueFromModel(value: String) {
        valueState.value = value
    }

    override fun setError(error: String?) {
        errorState.value = error
    }

    override fun setVisible(visible: Boolean) {
        visibleState.value = visible
    }

    override fun setReadonly(readonly: Boolean) {
        readonlyState.value = readonly
    }

    override fun setDescription(description: String?) {
        descriptionState.value = description
    }

    override fun setStyle(style: String?) {
        styleState.value = style
    }

    override fun observeChanges(listener: () -> Unit) {
        changeListeners += listener
    }

    override fun validate(): Boolean {
        if (!visible) return true
        val result = FieldValidators.validate(field.validators, value)
        errorState.value = result.error
        return result.valid
    }

    override fun collect(): String =
        formatters.formatterFor(field.type).server.toServer(value)

    override fun displayValue(): String =
        formatters.formatterFor(field.type).ui.toUi(value)
}

/**
 * Контроллер SELECT/RADIO: дополнительно реализует [ReferencesProtocol] — знает свой справочник и
 * выбранный элемент. Это «looking»-сторона стратегий description/visibility.
 */
class SelectFieldController(
    field: WfField,
    private val references: WfReferences,
    formatters: FormatterRegistry,
) : DefaultFieldController(field, formatters), ReferencesProtocol {

    val items: List<WfReferenceItem> get() = references.items(this.field.referenceId)

    override fun selectedReferenceItem(): WfReferenceItem? =
        references.item(this.field.referenceId, value)

    /** Показываем текст выбранной опции, а не её id. */
    override fun displayValue(): String = selectedReferenceItem()?.text ?: ""
}
