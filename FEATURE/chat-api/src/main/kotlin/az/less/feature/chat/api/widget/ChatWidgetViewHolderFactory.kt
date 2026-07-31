package az.less.feature.chat.api.widget

import android.view.ViewGroup

/**
 * Фабрика XML-вьюхолдера для одного типа виджета чата. Регистрируется фичей-поставщиком в реестр
 * `Map<String, ChatWidgetViewHolderFactory>` через `@Provides @IntoMap @ChatWidgets
 * @StringKey(ChatWidgetIds.X)` — чистая точка расширения: фича добавляет свой виджет,
 * не трогая ядро чата.
 */
interface ChatWidgetViewHolderFactory {

    fun create(parent: ViewGroup): ChatWidgetViewHolder
}
