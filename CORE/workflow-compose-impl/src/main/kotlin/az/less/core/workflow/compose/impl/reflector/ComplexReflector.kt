package az.less.core.workflow.compose.impl.reflector

import az.less.core.workflow.compose.api.model.FieldType
import az.less.core.workflow.compose.api.widget.FieldComposable
import az.less.core.workflow.compose.api.widget.FieldControllerFactory
import az.less.core.workflow.compose.api.widget.Reflector
import az.less.core.workflow.compose.api.widget.WidgetComposable
import az.less.core.workflow.compose.api.widget.WidgetRegistry
import az.less.core.workflow.compose.impl.controller.DefaultControllerFactories

/**
 * Центральный резолвер рендера (без кодогена).
 *
 * - Виджеты: сшивает несколько [WidgetRegistry] (ядро + фичи) **first-hit-wins**; неизвестный
 *   `widget.type` откатывается к дефолтному `FIELDSET`. Так фича добавляет виджет, не трогая ядро.
 * - Поля: рендерер и фабрика контроллера резолвятся по [FieldType] с дефолтами (read-only).
 */
class ComplexReflector(
    private val widgetRegistries: List<WidgetRegistry>,
    private val defaultWidgetRenderer: WidgetComposable,
    private val fieldRenderers: Map<FieldType, FieldComposable>,
    private val defaultFieldRenderer: FieldComposable,
) : Reflector {

    override fun widgetRenderer(type: String): WidgetComposable {
        widgetRegistries.forEach { registry ->
            registry.rendererFor(type)?.let { return it }
        }
        return defaultWidgetRenderer
    }

    override fun fieldRenderer(type: FieldType): FieldComposable =
        fieldRenderers[type] ?: defaultFieldRenderer

    override fun fieldControllerFactory(type: FieldType): FieldControllerFactory =
        DefaultControllerFactories.byType[type] ?: DefaultControllerFactories.default
}
