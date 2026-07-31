package az.less.feature.profile.impl.presentation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import az.less.core.di.api
import az.less.core.workflow.api.WorkflowFeatureApi
import az.less.core.workflow.api.WorkflowFlows
import az.less.core.workflow.compose.api.WorkflowComposeFeatureApi
import az.less.core.workflow.compose.api.WorkflowFlows as ComposeWorkflowFlows
import az.less.feature.catalog.api.CatalogFeatureApi
import az.less.feature.chat.api.ChatFeatureApi
import az.less.feature.profile.api.ProfileFeatureApi
import az.less.feature.transfer.api.TransferFeatureApi
import az.less.feature.profile.impl.R
import az.less.feature.profile.impl.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel by lazy {
        ProfileViewModel(api<ProfileFeatureApi>().profileRepository())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentProfileBinding.bind(view)
        binding.title.text = "Профиль"
        viewLifecycleOwner.lifecycleScope.launch {
            val profile = viewModel.load()
            binding.name.text = profile.name
            binding.email.text = profile.email
        }
        // Переход в соседнюю фичу через ЕЁ лаунчер (api берём из DI, не из :impl).
        binding.navNext.setOnClickListener {
            api<CatalogFeatureApi>().launcher().launch(parentFragmentManager)
        }
        // Server-driven форма редактирования профиля (кнопку добавляем программно).
        val editProfile = Button(requireContext()).apply {
            text = "Редактировать профиль (SDUI)"
            setOnClickListener {
                api<WorkflowFeatureApi>().launcher()
                    .launch(parentFragmentManager, WorkflowFlows.PROFILE_EDIT)
            }
        }
        (binding.root as ViewGroup).addView(editProfile)

        // Целиком-SDUI фича перевода: запускаем её лаунчером (а не флоу напрямую).
        val transfer = Button(requireContext()).apply {
            text = "Перевести деньги (SDUI)"
            setOnClickListener {
                api<TransferFeatureApi>().launcher().launch(parentFragmentManager)
            }
        }
        (binding.root as ViewGroup).addView(transfer)

        // Демо BDUI на Compose: свой Compose-рендер + межвиджетные стратегии.
        val composeWorkflow = Button(requireContext()).apply {
            text = "Открыть перевод (BDUI на Compose)"
            setOnClickListener {
                api<WorkflowComposeFeatureApi>().launcher()
                    .launch(parentFragmentManager, ComposeWorkflowFlows.PAYMENT)
            }
        }
        (binding.root as ViewGroup).addView(composeWorkflow)

        // Чат с ассистентом: экран-агрегатор виджетов из фич (@IntoMap/@IntoSet), два рендера.
        val chatXml = Button(requireContext()).apply {
            text = "Чат с ассистентом (XML)"
            setOnClickListener {
                api<ChatFeatureApi>().launcher().launchXml(parentFragmentManager)
            }
        }
        (binding.root as ViewGroup).addView(chatXml)

        val chatCompose = Button(requireContext()).apply {
            text = "Чат с ассистентом (Compose)"
            setOnClickListener {
                api<ChatFeatureApi>().launcher().launchCompose(parentFragmentManager)
            }
        }
        (binding.root as ViewGroup).addView(chatCompose)
    }
}
