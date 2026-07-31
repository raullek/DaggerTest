package az.less.core.designsystem.compose.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import az.less.core.designsystem.compose.theme.DsTheme

/** Главная кнопка-событие (как `ds_button`). */
@Composable
fun DsPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = DsTheme.dimens.fieldHeight),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = DsTheme.colors.primary,
            contentColor = DsTheme.colors.onPrimary,
        ),
    ) {
        Text(text)
    }
}

/** Вторичная кнопка-событие (как `ds_button_secondary`, например «Назад»/rollback). */
@Composable
fun DsSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = DsTheme.dimens.fieldHeight),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = DsTheme.colors.primary),
    ) {
        Text(text)
    }
}
