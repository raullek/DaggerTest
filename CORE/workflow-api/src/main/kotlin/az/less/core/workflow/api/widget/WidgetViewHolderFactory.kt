package az.less.core.workflow.api.widget

import android.view.ViewGroup

/**
 * Фабрика вьюхолдера для одного типа виджета. Регистрируется в реестр
 * `Map<String, WidgetViewHolderFactory>` через `@IntoMap @WidgetTypeKey("<тип>")` — это
 * точка расширения: фича добавляет свой виджет, не трогая ядро.
 */
interface WidgetViewHolderFactory {

    fun create(parent: ViewGroup): WidgetViewHolder
}
