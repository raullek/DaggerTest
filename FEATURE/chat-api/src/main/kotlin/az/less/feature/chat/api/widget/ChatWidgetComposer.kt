package az.less.feature.chat.api.widget

import androidx.compose.runtime.Composable
import az.less.feature.chat.api.model.WidgetMessage

/**
 * Compose-рендер одного типа виджета чата — параллельное поколение XML-фабрики
 * ([ChatWidgetViewHolderFactory]), регистрируется в СВОЮ карту
 * `Map<String, ChatWidgetComposer>` тем же ключом `@ChatWidgets @StringKey(ChatWidgetIds.X)`.
 * Одна фича вкладывает ОБА рендера — какой использовать, решает экран (XML/Compose).
 */
interface ChatWidgetComposer {

    /** Содержимое виджета внутри пузыря ассистента. */
    @Composable
    fun Content(message: WidgetMessage)
}
