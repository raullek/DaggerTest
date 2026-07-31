package az.less.core.designsystem.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import az.less.core.designsystem.compose.theme.DsTheme

/**
 * Презентационные stateless-компоненты дизайн-системы на Compose. Аналоги XML `ds_*` лейаутов, но
 * без инфлейта — BDUI-рендер на Compose биндит их напрямую. Каждый получает `value + onValueChange +
 * error + enabled`, своего состояния не держит.
 */

@Composable
fun DsTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String? = null,
) {
    Column(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            isError = error != null,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        )
        DsFieldError(error)
    }
}

/** Крупное денежное поле (как `ds_amount_field`): большой шрифт, числовая клавиатура, суффикс «₽». */
@Composable
fun DsAmountField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    currency: String = "₽",
) {
    Column(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            isError = error != null,
            enabled = enabled,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 28.sp),
            suffix = { Text(currency) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        DsFieldError(error)
    }
}

/** Поле-селектор: показывает выбранный текст, по клику отдаёт [onClick] (хост открывает список). */
@Composable
fun DsSelectField(
    label: String,
    displayText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    placeholder: String = "Выберите…",
) {
    Column(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .selectable(selected = false, enabled = enabled, onClick = onClick),
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            isError = error != null,
            enabled = false, // клик ловим через selectable, ввод запрещён
            readOnly = true,
            singleLine = true,
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
        )
        DsFieldError(error)
    }
}

@Composable
fun DsRadioGroup(
    label: String,
    options: List<DsOption>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
) {
    Column(modifier.fillMaxWidth()) {
        Text(label, color = DsTheme.colors.hint)
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = option.id == selectedId,
                        enabled = enabled,
                        role = Role.RadioButton,
                        onClick = { onSelect(option.id) },
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = option.id == selectedId, onClick = null, enabled = enabled)
                Text(option.text, Modifier.padding(start = 8.dp))
            }
        }
        DsFieldError(error)
    }
}

@Composable
fun DsCheckboxField(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .toggleable(
                    value = checked,
                    enabled = enabled,
                    role = Role.Checkbox,
                    onValueChange = onCheckedChange,
                )
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)
            Text(text, Modifier.padding(start = 8.dp))
        }
        DsFieldError(error)
    }
}

@Composable
fun DsSwitchField(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text)
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}

/** Опция для select/radio — id уходит на сервер, text показывается пользователю. */
data class DsOption(val id: String, val text: String)
