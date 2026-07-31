package az.less.feature.chat.impl.presentation.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import az.less.feature.chat.impl.di.chatInternalApi
import az.less.feature.chat.impl.presentation.ChatViewModel
import kotlinx.coroutines.launch

/** Compose-рендер чата: тот же домен (@PerFeature-репозиторий), другой слой отображения. */
internal class ChatComposeFragment : Fragment() {

    private val viewModel by lazy {
        ChatViewModel(chatInternalApi().interactor(), chatInternalApi().widgetRegistry())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MaterialTheme {
                val messages by viewModel.messages.collectAsStateWithLifecycle()
                ChatScreen(
                    messages = messages,
                    quickActions = viewModel.quickActions(),
                    registry = chatInternalApi().widgetRegistry(),
                    activity = requireActivity(),
                    onSend = { text ->
                        viewLifecycleOwner.lifecycleScope.launch { viewModel.send(text) }
                    },
                )
            }
        }
    }
}
