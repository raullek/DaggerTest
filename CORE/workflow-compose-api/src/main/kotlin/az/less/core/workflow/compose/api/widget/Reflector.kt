package az.less.core.workflow.compose.api.widget

import androidx.compose.runtime.Composable
import az.less.core.workflow.compose.api.model.FieldType
import az.less.core.workflow.compose.api.model.WfField
import az.less.core.workflow.compose.api.model.WfReferences

/**
 * Композабл-рендерер виджета (строка тела/header/footer) — просто
 * `@Composable`. Резолвится по `widget.type` через [Reflector].
 */
typealias WidgetComposable = @Composable (item: ScreenItem, interaction: WorkflowInteraction) -> Unit

/**
 * Композабл-рендерер поля внутри FIELDSET. Биндит контроллер к компоненту дизайн-системы.
 * Резолвится по `field.type`.
 */
typealias FieldComposable = @Composable (
    controller: FieldController,
    field: WfField,
    references: WfReferences,
) -> Unit

/**
 * Реестр виджет-рендереров: `widget.type -> WidgetComposable`. Один модуль (ядро или фича) кладёт
 * свой вклад через `@IntoMap @StringKey`. Несколько реестров сшиваются в [Reflector] (first-hit-wins).
 */
fun interface WidgetRegistry {
    fun rendererFor(type: String): WidgetComposable?
}

/**
 * Центральный резолвер рендера (без кодогена). Сшивает реестры виджетов
 * с дефолтным fallback'ом (`FIELDSET`), знает рендереры и фабрики контроллеров полей по [FieldType].
 *
 * - [widgetRenderer] — `widget.type` → композабл (неизвестный тип → дефолтный FIELDSET);
 * - [fieldRenderer] — `field.type` → композабл поля (неизвестный → read-only);
 * - [fieldControllerFactory] — `field.type` → фабрика контроллера (неизвестный → read-only-контроллер).
 */
interface Reflector {
    fun widgetRenderer(type: String): WidgetComposable
    fun fieldRenderer(type: FieldType): FieldComposable
    fun fieldControllerFactory(type: FieldType): FieldControllerFactory
}
