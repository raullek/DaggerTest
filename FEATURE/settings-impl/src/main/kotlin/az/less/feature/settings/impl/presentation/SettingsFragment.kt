package az.less.feature.settings.impl.presentation

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import az.less.core.di.api
import az.less.core.workflow.api.WorkflowFeatureApi
import az.less.core.workflow.api.WorkflowFlows
import az.less.core.workflow.compose.api.WorkflowComposeFeatureApi
import az.less.core.workflow.compose.api.WorkflowFlows as ComposeWorkflowFlows
import az.less.feature.profile.api.ProfileFeatureApi
import az.less.feature.settings.api.AppSettings
import az.less.feature.settings.api.SettingsFeatureApi
import az.less.feature.settings.impl.R
import az.less.feature.settings.impl.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel by lazy {
        SettingsViewModel(api<SettingsFeatureApi>().settingsRepository())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSettingsBinding.bind(view)
        binding.title.text = "Настройки"
        viewLifecycleOwner.lifecycleScope.launch {
            render(binding, viewModel.load())
        }
        binding.toggleTheme.setOnClickListener {
            render(binding, viewModel.toggleDarkTheme())
        }
        binding.navNext.setOnClickListener {
            api<ProfileFeatureApi>().launcher().launch(parentFragmentManager)
        }
        // Демо server-driven UI: кнопку добавляем программно, чтобы не трогать layout.
        val openWorkflow = Button(requireContext()).apply {
            text = "Открыть заявку (Server-Driven UI)"
            setOnClickListener {
                api<WorkflowFeatureApi>().launcher()
                    .launch(parentFragmentManager, WorkflowFlows.LOAN)
            }
        }
        (binding.root as android.view.ViewGroup).addView(openWorkflow)

        // Демо BDUI на Compose: отдельный движок со своим рендером + межвиджетными стратегиями.
        val openComposeWorkflow = Button(requireContext()).apply {
            text = "Открыть перевод (BDUI на Compose)"
            setOnClickListener {
                api<WorkflowComposeFeatureApi>().launcher()
                    .launch(parentFragmentManager, ComposeWorkflowFlows.PAYMENT)
            }
        }
        (binding.root as android.view.ViewGroup).addView(openComposeWorkflow)
    }

    private fun render(binding: FragmentSettingsBinding, settings: AppSettings) {
        binding.theme.text = "Тёмная тема: " + if (settings.darkTheme) "вкл" else "выкл"
        binding.notifications.text =
            "Уведомления: " + if (settings.notificationsEnabled) "вкл" else "выкл"
    }
}
