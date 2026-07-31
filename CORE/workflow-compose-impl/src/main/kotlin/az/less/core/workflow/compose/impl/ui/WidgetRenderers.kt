package az.less.core.workflow.compose.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import az.less.core.designsystem.compose.components.DsBanner
import az.less.core.designsystem.compose.components.DsCard
import az.less.core.designsystem.compose.components.DsPrimaryButton
import az.less.core.designsystem.compose.components.DsSecondaryButton
import az.less.core.designsystem.compose.components.DsSectionHeader
import az.less.core.designsystem.compose.components.DsStepper
import az.less.core.designsystem.compose.components.DsSummaryRow
import az.less.core.designsystem.compose.theme.DsTheme
import az.less.core.workflow.compose.api.widget.Reflector
import az.less.core.workflow.compose.api.widget.ScreenItem
import az.less.core.workflow.compose.api.widget.WidgetComposable
import az.less.core.workflow.compose.api.widget.WidgetRegistry

/** Reflector текущего экрана — раздаём через CompositionLocal, чтобы FIELDSET резолвил рендереры полей. */
val LocalWorkflowReflector = staticCompositionLocalOf<Reflector> {
    error("LocalWorkflowReflector не предоставлен")
}

/**
 * Compose-рендереры виджетов ядра. Каждый — [WidgetComposable]. Зарегистрированы в [CoreWidgetRegistry]
 * по `widget.type` (+ зарезервированные структурные ключи header/stepper/event). Фича добавляет свой
 * виджет своим [WidgetRegistry] — ядро не трогается (first-hit-wins в ComplexReflector).
 */
object WidgetRenderers {

    val FieldSet: WidgetComposable = { item, _ ->
        val reflector = LocalWorkflowReflector.current
        val widget = item.widget
        DsCard {
            Column(verticalArrangement = Arrangement.spacedBy(DsTheme.dimens.spaceM)) {
                widget?.title?.let { DsSectionHeader(it, subtitle = widget.description) }
                widget?.fields?.forEach { field ->
                    val controller = item.scope.controller(field.id) ?: return@forEach
                    if (controller.visible) {
                        reflector.fieldRenderer(field.type)(controller, field, item.references)
                    }
                }
            }
        }
    }

    val Summary: WidgetComposable = { item, _ ->
        val widget = item.widget
        DsCard {
            Column(verticalArrangement = Arrangement.spacedBy(DsTheme.dimens.spaceS)) {
                widget?.title?.let { DsSectionHeader(it) }
                widget?.fields?.forEach { field ->
                    val controller = item.scope.controller(field.id)
                    DsSummaryRow(field.title, controller?.displayValue().orEmpty().ifEmpty { field.value })
                }
            }
        }
    }

    val Banner: WidgetComposable = { item, _ ->
        val tone = item.widget?.properties?.string("tone")
        DsBanner(text = item.widget?.title.orEmpty(), success = tone == "success")
    }

    val Header: WidgetComposable = { item, _ ->
        DsSectionHeader(item.title.orEmpty(), subtitle = item.subtitle)
    }

    val Stepper: WidgetComposable = { item, _ ->
        DsStepper(step = item.step, steps = item.steps)
    }

    val Event: WidgetComposable = { item, interaction ->
        val event = item.event
        if (event != null) {
            if (event.isRollback) {
                DsSecondaryButton(text = event.title, onClick = { interaction.rollback() }, modifier = Modifier.fillMaxWidth())
            } else {
                DsPrimaryButton(text = event.title, onClick = { interaction.submit(event) }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/** Реестр виджет-рендереров ядра (`widget.type -> композабл`). */
object CoreWidgetRegistry : WidgetRegistry {
    private val map: Map<String, WidgetComposable> = mapOf(
        "FIELDSET" to WidgetRenderers.FieldSet,
        "SUMMARY" to WidgetRenderers.Summary,
        "BANNER" to WidgetRenderers.Banner,
        ScreenItem.TYPE_HEADER to WidgetRenderers.Header,
        ScreenItem.TYPE_STEPPER to WidgetRenderers.Stepper,
        ScreenItem.TYPE_EVENT to WidgetRenderers.Event,
    )

    override fun rendererFor(type: String): WidgetComposable? = map[type]
}
