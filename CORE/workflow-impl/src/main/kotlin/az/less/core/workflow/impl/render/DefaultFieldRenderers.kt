package az.less.core.workflow.impl.render

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import az.less.core.designsystem.DesignSystemInflater
import az.less.core.designsystem.DsComponent
import az.less.core.designsystem.dsCheckbox
import az.less.core.designsystem.dsCreateRadioButton
import az.less.core.designsystem.dsError
import az.less.core.designsystem.dsInput
import az.less.core.designsystem.dsLabel
import az.less.core.designsystem.dsRadioGroup
import az.less.core.designsystem.dsSwitch
import az.less.core.designsystem.dsSwitchText
import az.less.core.designsystem.dsValue
import az.less.core.workflow.api.format.FormatterRegistry
import az.less.core.workflow.api.model.WorkflowField
import az.less.core.workflow.api.model.WorkflowReferences
import az.less.core.workflow.api.widget.FieldRenderer
import az.less.core.workflow.api.widget.WidgetScope
import javax.inject.Inject

/**
 * Дефолтные рендереры полей. Каждый инфлейтит компонент дизайн-системы по ключу и биндит ввод
 * в [WidgetScope]. Это плоские классы — регистрируются в реестр `Map<String, FieldRenderer>`
 * через `@IntoMap` (см. WorkflowFieldsModule), поэтому фича может добавить рендерер нового типа,
 * не трогая ядро.
 */

/** Текстовый ввод (TEXT/INTEGER/DECIMAL/MONEY/PHONE/DATE/AMOUNT) с форматтером server↔ui. */
class InputFieldRenderer(
    private val formatters: FormatterRegistry,
    private val inputType: Int,
    private val defaultComponent: DsComponent,
) : FieldRenderer {

    override fun render(
        context: Context,
        field: WorkflowField,
        references: WorkflowReferences,
        scope: WidgetScope,
    ): View {
        // Сервер может задать вариант компонента через field.style (если он input-совместим).
        val component = DsComponent.from(field.style)?.takeIf { it.isInputCompatible() } ?: defaultComponent
        val view = DesignSystemInflater.inflate(context, component)
        view.dsLabel().text = field.title

        val formatter = formatters.formatterFor(field.type)
        val holder = scope.valueHolder(field)
        view.dsInput().apply {
            inputType = this@InputFieldRenderer.inputType
            isEnabled = !field.readonly
            setText(if (field.value.isEmpty()) "" else formatter.ui.toUi(field.value))
            addTextChangedListener { editable ->
                holder.updateFromUi(formatter.server.toServer(editable?.toString().orEmpty()))
                scope.errorHolder(field).update(null)
            }
        }
        bindFieldError(view, field, scope)
        return view
    }
}

/** Выбор из справочника диалогом (SELECT). */
class SelectFieldRenderer @Inject constructor() : FieldRenderer {

    override fun render(
        context: Context,
        field: WorkflowField,
        references: WorkflowReferences,
        scope: WidgetScope,
    ): View {
        val view = DesignSystemInflater.inflate(context, DsComponent.SELECT_FIELD)
        view.dsLabel().text = field.title

        val holder = scope.valueHolder(field)
        val items = references.itemsFor(field.referenceId)
        view.dsValue().apply {
            text = if (holder.get().isEmpty()) "Выберите…"
            else references.titleOf(field.referenceId, holder.get())
            isEnabled = !field.readonly
            setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle(field.title)
                    .setItems(items.map { it.text }.toTypedArray()) { _, index ->
                        val item = items[index]
                        holder.updateFromUi(item.id)
                        text = item.text
                        scope.errorHolder(field).update(null)
                    }
                    .show()
            }
        }
        bindFieldError(view, field, scope)
        return view
    }
}

/** Выбор из справочника радиокнопками (RADIO) — варианты видны сразу. */
class RadioFieldRenderer @Inject constructor() : FieldRenderer {

    override fun render(
        context: Context,
        field: WorkflowField,
        references: WorkflowReferences,
        scope: WidgetScope,
    ): View {
        val view = DesignSystemInflater.inflate(context, DsComponent.RADIO_FIELD)
        view.dsLabel().text = field.title

        val holder = scope.valueHolder(field)
        val items = references.itemsFor(field.referenceId)
        val group = view.dsRadioGroup()
        items.forEachIndexed { index, item ->
            group.addView(group.dsCreateRadioButton(id = index + 1, text = item.text))
            if (item.id == holder.get()) group.check(index + 1)
        }
        group.setOnCheckedChangeListener { _, checkedId ->
            items.getOrNull(checkedId - 1)?.let { holder.updateFromUi(it.id) }
            scope.errorHolder(field).update(null)
        }
        bindFieldError(view, field, scope)
        return view
    }
}

/** Чекбокс: хранит "true"/"" (пусто ловит Required). */
class CheckboxFieldRenderer @Inject constructor() : FieldRenderer {

    override fun render(
        context: Context,
        field: WorkflowField,
        references: WorkflowReferences,
        scope: WidgetScope,
    ): View {
        val view = DesignSystemInflater.inflate(context, DsComponent.CHECKBOX_FIELD)
        val holder = scope.valueHolder(field)
        view.dsCheckbox().apply {
            text = field.title
            isEnabled = !field.readonly
            isChecked = holder.get() == "true"
            setOnCheckedChangeListener { _, checked ->
                holder.updateFromUi(if (checked) "true" else "")
                scope.errorHolder(field).update(null)
            }
        }
        bindFieldError(view, field, scope)
        return view
    }
}

/** Переключатель (SWITCH): хранит "true"/"". */
class SwitchFieldRenderer @Inject constructor() : FieldRenderer {

    override fun render(
        context: Context,
        field: WorkflowField,
        references: WorkflowReferences,
        scope: WidgetScope,
    ): View {
        val view = DesignSystemInflater.inflate(context, DsComponent.SWITCH_FIELD)
        val holder = scope.valueHolder(field)
        view.dsSwitchText().text = field.title
        view.dsSwitch().apply {
            isEnabled = !field.readonly
            isChecked = holder.get() == "true"
            setOnCheckedChangeListener { _, checked ->
                holder.updateFromUi(if (checked) "true" else "")
            }
        }
        bindFieldError(view, field, scope)
        return view
    }
}

/** Только для чтения: текстовый компонент с выключенным вводом. */
class ReadonlyFieldRenderer : FieldRenderer {

    override fun render(
        context: Context,
        field: WorkflowField,
        references: WorkflowReferences,
        scope: WidgetScope,
    ): View {
        val view = DesignSystemInflater.inflate(context, DsComponent.TEXT_FIELD)
        view.dsLabel().text = field.title
        view.dsInput().apply {
            setText(field.value)
            isEnabled = false
        }
        bindFieldError(view, field, scope)
        return view
    }
}

/** Только компоненты с полем ввода годятся под input-рендерер. */
private fun DsComponent.isInputCompatible(): Boolean =
    this == DsComponent.TEXT_FIELD || this == DsComponent.MONEY_FIELD ||
        this == DsComponent.DATE_FIELD || this == DsComponent.AMOUNT_FIELD

/** Подписать блок ошибки компонента на error-holder поля (хост кладёт туда текст ошибки). */
internal fun bindFieldError(componentView: View, field: WorkflowField, scope: WidgetScope) {
    val errorView = componentView.dsError()
    scope.errorHolder(field).observeUi { message ->
        errorView.text = message.orEmpty()
        errorView.visibility = if (message.isNullOrEmpty()) View.GONE else View.VISIBLE
    }
}
