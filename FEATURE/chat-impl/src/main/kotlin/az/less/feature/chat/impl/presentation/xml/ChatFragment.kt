package az.less.feature.chat.impl.presentation.xml

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import az.less.feature.chat.impl.R
import az.less.feature.chat.impl.databinding.FragmentChatBinding
import az.less.feature.chat.impl.di.chatInternalApi
import az.less.feature.chat.impl.presentation.ChatViewModel
import kotlinx.coroutines.launch

/** XML-рендер чата: RecyclerView + вьюхолдеры из фабрик фич. */
internal class ChatFragment : Fragment(R.layout.fragment_chat) {

    private val viewModel by lazy {
        ChatViewModel(chatInternalApi().interactor(), chatInternalApi().widgetRegistry())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentChatBinding.bind(view)

        val adapter = ChatAdapter(chatInternalApi().widgetRegistry())
        binding.messages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.messages.adapter = adapter

        // Лента: подписка на StateFlow домена + автоскролл к последнему сообщению.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                adapter.submitList(messages) {
                    if (messages.isNotEmpty()) binding.messages.scrollToPosition(messages.size - 1)
                }
            }
        }

        // Чипы быстрых действий: каждое несёт навигацию с собой (onClick → лаунчер своей фичи).
        viewModel.quickActions().forEach { action ->
            binding.quickActions.addView(
                Button(requireContext()).apply {
                    text = action.title
                    setOnClickListener { action.onClick(requireActivity()) }
                },
            )
        }

        binding.send.setOnClickListener {
            val text = binding.input.text.toString()
            binding.input.text.clear()
            viewLifecycleOwner.lifecycleScope.launch { viewModel.send(text) }
        }
    }
}
