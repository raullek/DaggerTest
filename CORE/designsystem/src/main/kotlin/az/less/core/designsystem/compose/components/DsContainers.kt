package az.less.core.designsystem.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import az.less.core.designsystem.compose.theme.DsTheme

/** Текст ошибки под полем (как `ds_error`). Скрыт, если ошибки нет. */
@Composable
fun DsFieldError(error: String?) {
    if (!error.isNullOrEmpty()) {
        Text(
            text = error,
            color = DsTheme.colors.error,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp),
        )
    }
}

/** Карточка-контейнер группы полей (как `ds_card`). */
@Composable
fun DsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DsTheme.dimens.corner),
        colors = CardDefaults.cardColors(containerColor = DsTheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.padding(DsTheme.dimens.spaceL), content = { content() })
    }
}

/** Заголовок секции (как `ds_section_header`). */
@Composable
fun DsSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        if (!subtitle.isNullOrEmpty()) {
            Text(subtitle, color = DsTheme.colors.hint, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/** Строка «label ↔ value» для сводки (как `ds_summary_row`). */
@Composable
fun DsSummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = DsTheme.colors.hint)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

/** Баннер info/success (как `ds_banner` / `ds_banner_success`). */
@Composable
fun DsBanner(
    text: String,
    modifier: Modifier = Modifier,
    success: Boolean = false,
) {
    val bg = if (success) DsTheme.colors.successBg else DsTheme.colors.infoBg
    val accent = if (success) DsTheme.colors.success else DsTheme.colors.primary
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DsTheme.dimens.corner))
            .background(bg)
            .padding(DsTheme.dimens.spaceL),
    ) {
        Text(text, color = accent)
    }
}

/** Степпер-точки прогресса (как `ds_stepper`): активные/неактивные шаги. */
@Composable
fun DsStepper(
    step: Int,
    steps: Int,
    modifier: Modifier = Modifier,
) {
    if (steps <= 0) return
    Row(
        modifier.padding(vertical = DsTheme.dimens.spaceM),
        horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.spaceM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 1..steps) {
            val active = i <= step
            Box(
                Modifier
                    .size(DsTheme.dimens.dot)
                    .clip(CircleShape)
                    .background(if (active) DsTheme.colors.primary else DsTheme.colors.outline),
            )
        }
    }
}
