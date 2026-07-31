package az.less.feature.catalog.impl.presentation

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import az.less.core.di.api
import az.less.core.workflow.api.WorkflowFeatureApi
import az.less.core.workflow.api.WorkflowFlows
import az.less.feature.catalog.api.CatalogFeatureApi
import az.less.feature.catalog.impl.R
import az.less.feature.catalog.impl.databinding.FragmentCatalogBinding
import az.less.feature.settings.api.SettingsFeatureApi
import kotlinx.coroutines.launch

class CatalogFragment : Fragment(R.layout.fragment_catalog) {

    private val viewModel by lazy {
        CatalogViewModel(api<CatalogFeatureApi>().productRepository())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCatalogBinding.bind(view)
        binding.title.text = "Каталог продуктов"
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.load().forEach { product ->
                val row = TextView(requireContext()).apply {
                    textSize = 16f
                    text = if (product.price == 0) {
                        "• ${product.title} — бесплатно"
                    } else {
                        "• ${product.title} — ${product.price} ₽/мес"
                    }
                }
                binding.list.addView(row, binding.list.childCount - 1)
            }
        }
        binding.navNext.setOnClickListener {
            api<SettingsFeatureApi>().launcher().launch(parentFragmentManager)
        }
        // Server-driven форма отзыва (кнопку добавляем программно).
        val leaveFeedback = Button(requireContext()).apply {
            text = "Оставить отзыв (SDUI)"
            setOnClickListener {
                api<WorkflowFeatureApi>().launcher()
                    .launch(parentFragmentManager, WorkflowFlows.FEEDBACK)
            }
        }
        binding.list.addView(leaveFeedback)
    }
}
