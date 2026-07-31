package az.less.core.workflow.impl.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import az.less.core.workflow.api.widget.ScreenItem
import az.less.core.workflow.api.widget.WidgetViewHolder
import az.less.core.workflow.api.widget.WidgetViewHolderFactory
import az.less.core.workflow.api.widget.WorkflowInteraction

/**
 * RecyclerView-адаптер экрана. Резолвит `ScreenItem.typeKey → WidgetViewHolderFactory` из реестра
 * (собранного мультибиндингами ядра и фич) и присваивает каждому ключу стабильный `viewType`.
 * Фабрики приходят из DI-реестра.
 */
class WorkflowScreenAdapter(
    private val factories: Map<String, WidgetViewHolderFactory>,
    private val interaction: WorkflowInteraction,
) : RecyclerView.Adapter<WidgetViewHolder>() {

    private val typeKeys: List<String> = factories.keys.toList() // индекс ↔ ключ типа
    private val items = mutableListOf<ScreenItem>()

    fun hasFactory(typeKey: String): Boolean = factories.containsKey(typeKey)

    fun submit(newItems: List<ScreenItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged() // экран приходит целиком — полная замена списка
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = typeKeys.indexOf(items[position].typeKey)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val key = typeKeys[viewType]
        return factories.getValue(key).create(parent)
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.bind(items[position], interaction)
    }
}
