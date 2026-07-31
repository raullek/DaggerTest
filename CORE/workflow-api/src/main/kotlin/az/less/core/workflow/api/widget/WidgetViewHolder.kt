package az.less.core.workflow.api.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Базовый вьюхолдер виджета в RecyclerView экрана.
 * Конкретный вьюхолдер биндит [ScreenItem] (виджет/поле/кнопку) и при необходимости
 * дёргает [WorkflowInteraction].
 */
abstract class WidgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: ScreenItem, interaction: WorkflowInteraction)
}
