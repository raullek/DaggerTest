package az.less.core.workflow.api.widget

import android.content.Context
import android.view.View
import az.less.core.workflow.api.model.WorkflowField
import az.less.core.workflow.api.model.WorkflowReferences

/**
 * Превращает одно поле модели в Android-View (инфлейтит компонент дизайн-системы и биндит ввод
 * в [WidgetScope]).
 *
 * Регистрируется в реестр `Map<String, FieldRenderer>` по строковому типу поля через `@IntoMap`
 * — фича может добавить рендерер нового типа поля, не трогая ядро. Используется вьюхолдером
 * виджета-группы (FIELDSET), который складывает отрендеренные поля в свою карточку.
 */
interface FieldRenderer {

    fun render(
        context: Context,
        field: WorkflowField,
        references: WorkflowReferences,
        scope: WidgetScope,
    ): View
}
