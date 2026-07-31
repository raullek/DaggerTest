package az.less.core.workflow.compose.impl.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import az.less.core.designsystem.compose.components.DsAmountField
import az.less.core.designsystem.compose.components.DsCheckboxField
import az.less.core.designsystem.compose.components.DsOption
import az.less.core.designsystem.compose.components.DsRadioGroup
import az.less.core.designsystem.compose.components.DsSelectField
import az.less.core.designsystem.compose.components.DsSwitchField
import az.less.core.designsystem.compose.components.DsTextField
import az.less.core.designsystem.compose.theme.DsTheme
import az.less.core.workflow.compose.api.model.FieldType
import az.less.core.workflow.compose.api.protocol.DescriptionProtocol
import az.less.core.workflow.compose.api.widget.FieldComposable
import az.less.core.workflow.compose.impl.controller.SelectFieldController

/**
 * Compose-рендереры полей по [FieldType]: биндят контроллер к компоненту дизайн-системы. Читают
 * Compose-State контроллера (`value`/`error`/`description`/`readonly`) → реактивно перерисовываются,
 * в т.ч. от эффектов стратегий. Ввод уходит в `controller.onUiInput` (UI-канал).
 *
 * Карта `field.type -> FieldComposable` отдаётся в Reflector; неизвестный тип → read-only.
 */
object FieldRenderers {

    // --- input (TEXT/INTEGER/DECIMAL/DATE/PHONE) ---
    private fun inputRenderer(keyboard: KeyboardType): FieldComposable =
        { controller, field, _ ->
            // description может реактивно меняться стратегией → берём с контроллера.
            val hint = (controller as? DescriptionProtocol)?.description ?: field.description
            DsTextField(
                label = controller.title,
                value = controller.value,
                onValueChange = controller::onUiInput,
                error = controller.error,
                enabled = !controller.readonly,
                keyboardType = keyboard,
                placeholder = hint,
            )
        }

    // --- MONEY: крупное денежное поле (по style AMOUNT_FIELD), либо обычное числовое ---
    private val MoneyRenderer: FieldComposable = { controller, field, _ ->
        if (field.style.equals("AMOUNT_FIELD", ignoreCase = true)) {
            DsAmountField(
                label = controller.title,
                value = controller.value,
                onValueChange = controller::onUiInput,
                error = controller.error,
                enabled = !controller.readonly,
            )
        } else {
            DsTextField(
                label = controller.title,
                value = controller.value,
                onValueChange = controller::onUiInput,
                error = controller.error,
                enabled = !controller.readonly,
                keyboardType = KeyboardType.Number,
            )
        }
    }

    // --- SELECT: выпадающий список из справочника ---
    private val SelectRenderer: FieldComposable = { controller, _, _ ->
        var expanded by remember { mutableStateOf(false) }
        val select = controller as? SelectFieldController
        Column(Modifier.fillMaxWidth()) {
            DsSelectField(
                label = controller.title,
                displayText = controller.displayValue(),
                onClick = { expanded = true },
                error = controller.error,
                enabled = !controller.readonly,
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                select?.items?.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.text) },
                        onClick = {
                            controller.onUiInput(item.id)
                            expanded = false
                        },
                    )
                }
            }
        }
    }

    // --- RADIO: справочник кнопками-радио ---
    private val RadioRenderer: FieldComposable = { controller, _, _ ->
        val select = controller as? SelectFieldController
        DsRadioGroup(
            label = controller.title,
            options = select?.items?.map { DsOption(it.id, it.text) }.orEmpty(),
            selectedId = controller.value.ifEmpty { null },
            onSelect = controller::onUiInput,
            error = controller.error,
            enabled = !controller.readonly,
        )
    }

    private val CheckboxRenderer: FieldComposable = { controller, _, _ ->
        DsCheckboxField(
            text = controller.title,
            checked = controller.value.toBoolean(),
            onCheckedChange = { controller.onUiInput(it.toString()) },
            error = controller.error,
            enabled = !controller.readonly,
        )
    }

    private val SwitchRenderer: FieldComposable = { controller, _, _ ->
        DsSwitchField(
            text = controller.title,
            checked = controller.value.toBoolean(),
            onCheckedChange = { controller.onUiInput(it.toString()) },
            enabled = !controller.readonly,
        )
    }

    private val ReadonlyRenderer: FieldComposable = { controller, _, _ ->
        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text(controller.title, color = DsTheme.colors.hint)
            Text(controller.displayValue().ifEmpty { controller.value })
        }
    }

    val default: FieldComposable = ReadonlyRenderer

    // Карта объявлена ПОСЛЕ рендереров — иначе forward-reference к ещё не инициализированным val.
    val byType: Map<FieldType, FieldComposable> = mapOf(
        FieldType.TEXT to inputRenderer(KeyboardType.Text),
        FieldType.INTEGER to inputRenderer(KeyboardType.Number),
        FieldType.DECIMAL to inputRenderer(KeyboardType.Decimal),
        FieldType.DATE to inputRenderer(KeyboardType.Number),
        FieldType.PHONE to inputRenderer(KeyboardType.Phone),
        FieldType.MONEY to MoneyRenderer,
        FieldType.SELECT to SelectRenderer,
        FieldType.RADIO to RadioRenderer,
        FieldType.CHECKBOX to CheckboxRenderer,
        FieldType.SWITCH to SwitchRenderer,
        FieldType.UNKNOWN to ReadonlyRenderer,
    )
}
