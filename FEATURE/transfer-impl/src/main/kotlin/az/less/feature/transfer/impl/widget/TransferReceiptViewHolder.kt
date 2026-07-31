package az.less.feature.transfer.impl.widget

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import az.less.core.designsystem.DesignSystemInflater
import az.less.core.designsystem.DsComponent
import az.less.core.designsystem.dsContainer
import az.less.core.designsystem.dsSummaryLabel
import az.less.core.designsystem.dsSummaryValue
import az.less.core.workflow.api.widget.ScreenItem
import az.less.core.workflow.api.widget.WidgetViewHolder
import az.less.core.workflow.api.widget.WidgetViewHolderFactory
import az.less.core.workflow.api.widget.WorkflowInteraction
import javax.inject.Inject

/**
 * КАСТОМНЫЙ виджет фичи: «квитанция» перевода на экране успеха. Ядро workflow про него не знает —
 * фича регистрирует его в реестр через `@IntoMap @WidgetTypeKey("TRANSFER_RECEIPT")`
 * (см. TransferWidgetsModule). Это доказательство расширяемости: фича добавляет виджет, не трогая ядро.
 *
 * Данные приходят в `widget.properties` (amount/recipient/account). Виджет переиспользует компоненты
 * дизайн-системы (карточка + строки саммари).
 */
class TransferReceiptViewHolder(itemView: View) : WidgetViewHolder(itemView) {

    override fun bind(item: ScreenItem, interaction: WorkflowInteraction) {
        val props = item.widget?.properties ?: return
        val container = itemView.dsContainer()
        container.removeAllViews()

        val kopecks = props["amount"]?.toLongOrNull() ?: 0L
        container.addView(
            TextView(itemView.context).apply {
                text = "− ${kopecks / 100} ₽"
                textSize = 28f
                gravity = Gravity.CENTER
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            },
        )
        addRow(container, "Получателю", "+${props["recipient"].orEmpty()}")
        addRow(container, "Со счёта", props["account"].orEmpty())
    }

    private fun addRow(container: ViewGroup, label: String, value: String) {
        val row = DesignSystemInflater.inflate(itemView.context, DsComponent.SUMMARY_ROW)
        row.dsSummaryLabel().text = label
        row.dsSummaryValue().text = value
        container.addView(row)
    }
}

class TransferReceiptViewHolderFactory @Inject constructor() : WidgetViewHolderFactory {

    override fun create(parent: ViewGroup): WidgetViewHolder {
        val view = DesignSystemInflater.inflate(parent.context, DsComponent.CARD, parent, false)
        return TransferReceiptViewHolder(view)
    }
}
