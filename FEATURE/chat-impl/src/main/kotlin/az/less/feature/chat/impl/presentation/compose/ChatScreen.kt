package az.less.feature.chat.impl.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import az.less.feature.chat.api.action.ChatQuickAction
import az.less.feature.chat.api.model.ChatMessage
import az.less.feature.chat.api.model.TextMessage
import az.less.feature.chat.api.model.WidgetMessage
import az.less.feature.chat.impl.domain.ChatWidgetRegistry

/**
 * Compose-рендер чата поверх того же домена, что и XML. Виджет-сообщения резолвятся по
 * server-driven id через `Map<String, ChatWidgetComposer>` из вкладов фич; неизвестный id —
 * фолбэк-карточка (та же стратегия key-lookup + fallback, что в XML-адаптере).
 */
@Composable
internal fun ChatScreen(
    messages: List<ChatMessage>,
    quickActions: List<ChatQuickAction>,
    registry: ChatWidgetRegistry,
    activity: FragmentActivity,
    onSend: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Ассистент (Compose)",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp),
        )

        val listState = rememberLazyListState()
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(messages, key = { it.id }) { message ->
                when (message) {
                    is TextMessage -> TextBubble(message)
                    is WidgetMessage -> WidgetBubble(message, registry)
                }
            }
        }

        // Чипы быстрых действий: Set<ChatQuickAction> из вкладов фич, навигация внутри действия.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            quickActions.forEach { action ->
                OutlinedButton(onClick = { action.onClick(activity) }) {
                    Text(action.title)
                }
            }
        }

        var input by remember { mutableStateOf("") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Сообщение…") },
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = {
                    onSend(input)
                    input = ""
                },
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Text("Отправить")
            }
        }
    }
}

@Composable
private fun TextBubble(message: TextMessage) {
    Row(modifier = Modifier.fillMaxWidth()) {
        if (!message.incoming) Box(modifier = Modifier.weight(1f))
        Text(
            text = message.text,
            color = if (message.incoming) Color(0xFF1B1F23) else Color.White,
            modifier = Modifier
                .background(
                    color = if (message.incoming) Color(0xFFEFF1F4) else Color(0xFF21A038),
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(12.dp),
        )
        if (message.incoming) Box(modifier = Modifier.weight(1f))
    }
}

/** Слот виджета: содержимое рисует фича-поставщик; неизвестный id → заглушка. */
@Composable
private fun WidgetBubble(message: WidgetMessage, registry: ChatWidgetRegistry) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 32.dp)
            .border(1.dp, Color(0xFFD7DBE0), RoundedCornerShape(16.dp))
            .padding(12.dp),
    ) {
        val composer = registry.composer(message.widgetId)
        if (composer != null) {
            composer.Content(message)
        } else {
            Text(
                text = "Виджет «${message.widgetId}» не поддерживается этой версией приложения",
                color = Color(0xFF6B7280),
            )
        }
    }
}
