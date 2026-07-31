package az.less.core.designsystem.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Семантические токены цвета дизайн-системы (зеркало `res/values/colors.xml`, чтобы XML- и
 * Compose-компоненты выглядели одинаково). Раздаются через [LocalDsColors], а не хардкодятся в
 * компонентах — так тема переключается централизованно (light/dark).
 */
data class DsColors(
    val primary: Color,
    val onPrimary: Color,
    val surface: Color,
    val onSurface: Color,
    val outline: Color,
    val error: Color,
    val hint: Color,
    val success: Color,
    val successBg: Color,
    val infoBg: Color,
    val divider: Color,
)

private val LightDsColors = DsColors(
    primary = Color(0xFF1A73E8),
    onPrimary = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F1F1F),
    outline = Color(0xFFC4C7C5),
    error = Color(0xFFD32F2F),
    hint = Color(0xFF5F6368),
    success = Color(0xFF1E8E3E),
    successBg = Color(0xFFE6F4EA),
    infoBg = Color(0xFFE8F0FE),
    divider = Color(0xFFE8EAED),
)

private val DarkDsColors = DsColors(
    primary = Color(0xFF8AB4F8),
    onPrimary = Color(0xFF06122B),
    surface = Color(0xFF1B1B1F),
    onSurface = Color(0xFFE3E3E6),
    outline = Color(0xFF45474A),
    error = Color(0xFFF2B8B5),
    hint = Color(0xFF9AA0A6),
    success = Color(0xFF81C995),
    successBg = Color(0xFF1B3326),
    infoBg = Color(0xFF1C2A40),
    divider = Color(0xFF303134),
)

/** Размерные токены (зеркало `res/values/dimens.xml`). */
data class DsDimens(
    val spaceS: Dp = 4.dp,
    val spaceM: Dp = 8.dp,
    val spaceL: Dp = 16.dp,
    val corner: Dp = 8.dp,
    val fieldHeight: Dp = 48.dp,
    val dot: Dp = 8.dp,
)

val LocalDsColors = staticCompositionLocalOf { LightDsColors }
val LocalDsDimens = staticCompositionLocalOf { DsDimens() }

/** Точка доступа к токенам из компонентов: `DsTheme.colors.primary`, `DsTheme.dimens.spaceL`. */
object DsTheme {
    val colors: DsColors
        @Composable @ReadOnlyComposable get() = LocalDsColors.current
    val dimens: DsDimens
        @Composable @ReadOnlyComposable get() = LocalDsDimens.current
}

private val DsTypography = Typography(
    titleLarge = Typography().titleLarge.copy(fontSize = 22.sp),
    bodyLarge = Typography().bodyLarge.copy(fontSize = 16.sp),
    labelMedium = Typography().labelMedium.copy(fontSize = 13.sp),
)

/**
 * Корневая тема дизайн-системы для Compose. Оборачивает [MaterialTheme] и кладёт семантические
 * токены в CompositionLocal. Все BDUI-экраны на Compose рисуются внутри неё.
 */
@Composable
fun DsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val dsColors = if (darkTheme) DarkDsColors else LightDsColors
    val material = if (darkTheme) {
        darkColorScheme(
            primary = dsColors.primary,
            onPrimary = dsColors.onPrimary,
            surface = dsColors.surface,
            onSurface = dsColors.onSurface,
            error = dsColors.error,
            outline = dsColors.outline,
            background = dsColors.surface,
        )
    } else {
        lightColorScheme(
            primary = dsColors.primary,
            onPrimary = dsColors.onPrimary,
            surface = dsColors.surface,
            onSurface = dsColors.onSurface,
            error = dsColors.error,
            outline = dsColors.outline,
            background = dsColors.surface,
        )
    }
    CompositionLocalProvider(
        LocalDsColors provides dsColors,
        LocalDsDimens provides DsDimens(),
    ) {
        MaterialTheme(colorScheme = material, typography = DsTypography, content = content)
    }
}
