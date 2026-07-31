package az.less.core.workflow.impl.render

import android.view.View
import android.view.ViewGroup
import az.less.core.designsystem.DesignSystemInflater
import az.less.core.designsystem.DsComponent
import az.less.core.designsystem.dsBannerText
import az.less.core.designsystem.dsContainer
import az.less.core.designsystem.dsSummaryLabel
import az.less.core.designsystem.dsSummaryValue
import az.less.core.designsystem.dsTitle
import az.less.core.workflow.api.format.FormatterRegistry
import az.less.core.workflow.api.widget.ScreenItem
import az.less.core.workflow.api.widget.WidgetViewHolder
import az.less.core.workflow.api.widget.WidgetViewHolderFactory
import az.less.core.workflow.api.widget.WorkflowInteraction
import javax.inject.Inject

/**
 * Вьюхолдер саммари (SUMMARY): карточка ДС со строками «подпись ↔ значение» (read-only).
 * Значения форматируются через [FormatterRegistry] по типу поля.
 */
class SummaryViewHolder(
    itemView: View,
    private val formatters: FormatterRegistry,
) : WidgetViewHolder(itemView) {

    override fun bind(item: ScreenItem, interaction: WorkflowInteraction) {
        val widget = item.widget ?: return
        val container = itemView.dsContainer()
        container.removeAllViews()

        widget.title?.let { title ->
            val header = DesignSystemInflater.inflate(itemView.context, DsComponent.SECTION_HEADER)
            header.dsTitle().text = title
            container.addView(header)
        }
        widget.fields.forEach { field ->
            val row = DesignSystemInflater.inflate(itemView.context, DsComponent.SUMMARY_ROW)
            row.dsSummaryLabel().text = field.title
            row.dsSummaryValue().text =
                if (field.value.isEmpty()) "—" else formatters.formatterFor(field.type).ui.toUi(field.value)
            container.addView(row)
        }
    }
}

class SummaryViewHolderFactory @Inject constructor(
    private val formatters: FormatterRegistry,
) : WidgetViewHolderFactory {

    override fun create(parent: ViewGroup): WidgetViewHolder {
        val view = DesignSystemInflater.inflate(parent.context, DsComponent.CARD, parent, false)
        return SummaryViewHolder(view, formatters)
    }
}

/**
 * Вьюхолдер баннера (BANNER): info или success по `widget.properties["tone"]`. Тон известен только
 * на bind, поэтому корень — контейнер, в который инфлейтится нужный вариант компонента.
 */
class BannerViewHolder(itemView: ViewGroup) : WidgetViewHolder(itemView) {

    override fun bind(item: ScreenItem, interaction: WorkflowInteraction) {
        val container = itemView as ViewGroup
        container.removeAllViews()
        val widget = item.widget ?: return
        val success = widget.properties["tone"].equals("success", ignoreCase = true)
        val banner = DesignSystemInflater.inflate(
            itemView.context,
            if (success) DsComponent.BANNER_SUCCESS else DsComponent.BANNER,
        )
        banner.dsBannerText().text = widget.title ?: widget.description.orEmpty()
        container.addView(banner)
    }
}

class BannerViewHolderFactory @Inject constructor() : WidgetViewHolderFactory {

    override fun create(parent: ViewGroup): WidgetViewHolder =
        BannerViewHolder(itemContainer(parent))
}
