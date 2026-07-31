package az.less.core.workflow.impl.render

import android.view.View
import android.view.ViewGroup
import az.less.core.designsystem.DesignSystemInflater
import az.less.core.designsystem.DsComponent
import az.less.core.designsystem.dsContainer
import az.less.core.designsystem.dsSubtitle
import az.less.core.designsystem.dsTitle
import az.less.core.workflow.api.widget.FieldRenderer
import az.less.core.workflow.api.widget.ScreenItem
import az.less.core.workflow.api.widget.WidgetViewHolder
import az.less.core.workflow.api.widget.WidgetViewHolderFactory
import az.less.core.workflow.api.widget.WorkflowInteraction
import javax.inject.Inject

/**
 * Вьюхолдер виджета-группы (FIELDSET): карточка ДС с заголовком и полями. Поля рендерит через
 * РЕЕСТР [FieldRenderer] (`field.type -> рендерер`), поэтому новый тип поля добавляется без
 * правок этого вьюхолдера.
 */
class FieldSetViewHolder(
    itemView: View,
    private val fieldRenderers: Map<String, FieldRenderer>,
    private val readonlyRenderer: FieldRenderer,
) : WidgetViewHolder(itemView) {

    override fun bind(item: ScreenItem, interaction: WorkflowInteraction) {
        val widget = item.widget ?: return
        val scope = item.scope ?: return
        val container = itemView.dsContainer()
        container.removeAllViews()

        widget.title?.let { title ->
            val header = DesignSystemInflater.inflate(itemView.context, DsComponent.SECTION_HEADER)
            header.dsTitle().text = title
            widget.description?.let { header.dsSubtitle().apply { text = it; visibility = View.VISIBLE } }
            container.addView(header)
        }

        widget.fields.forEach { field ->
            val renderer = when {
                field.readonly -> readonlyRenderer
                else -> fieldRenderers[field.type.name] ?: readonlyRenderer
            }
            container.addView(renderer.render(itemView.context, field, item.references, scope))
        }
    }
}

/** Фабрика FIELDSET-вьюхолдера. Реестр полей приходит мультибиндингом. */
class FieldSetViewHolderFactory @Inject constructor(
    private val fieldRenderers: Map<String, @JvmSuppressWildcards FieldRenderer>,
) : WidgetViewHolderFactory {

    private val readonlyRenderer = ReadonlyFieldRenderer()

    override fun create(parent: ViewGroup): WidgetViewHolder {
        val view = DesignSystemInflater.inflate(parent.context, DsComponent.CARD, parent, false)
        return FieldSetViewHolder(view, fieldRenderers, readonlyRenderer)
    }
}
