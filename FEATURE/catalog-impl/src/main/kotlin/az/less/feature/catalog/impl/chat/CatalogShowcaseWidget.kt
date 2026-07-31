package az.less.feature.catalog.impl.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.doOnAttach
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import az.less.core.di.api
import az.less.feature.catalog.api.CatalogFeatureApi
import az.less.feature.catalog.api.Product
import az.less.feature.chat.api.model.WidgetMessage
import az.less.feature.chat.api.widget.ChatWidgetComposer
import az.less.feature.chat.api.widget.ChatWidgetViewHolder
import az.less.feature.chat.api.widget.ChatWidgetViewHolderFactory
import az.less.feature.chat.api.widget.findFragmentActivity
import az.less.feature.catalog.impl.R
import kotlinx.coroutines.launch

/**
 * Виджет «витрина товаров» — вклад фичи catalog в чат. Демонстрирует server-driven payload:
 * сервер шлёт `limit`, сколько товаров показать, — виджет читает его из [WidgetMessage.payload].
 */

/** XML-рендер. */
internal class CatalogShowcaseViewHolderFactory : ChatWidgetViewHolderFactory {

    override fun create(parent: ViewGroup): ChatWidgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_widget_catalog, parent, false)
        return object : ChatWidgetViewHolder(view) {
            private val products = view.findViewById<LinearLayout>(R.id.products)
            private val open = view.findViewById<Button>(R.id.open)

            override fun bind(message: WidgetMessage) {
                val limit = message.payload["limit"]?.toIntOrNull() ?: DEFAULT_LIMIT
                products.removeAllViews()
                itemView.doOnAttach {
                    val owner = itemView.findViewTreeLifecycleOwner() ?: return@doOnAttach
                    owner.lifecycleScope.launch {
                        val top = api<CatalogFeatureApi>().productRepository().getProducts().take(limit)
                        products.removeAllViews()
                        top.forEach { product ->
                            products.addView(
                                TextView(itemView.context).apply {
                                    text = "• ${product.title} — ${product.price} ₽"
                                    textSize = 14f
                                },
                            )
                        }
                    }
                }
                open.setOnClickListener { view ->
                    view.context.findFragmentActivity()?.let {
                        api<CatalogFeatureApi>().launcher().launch(it.supportFragmentManager)
                    }
                }
            }
        }
    }
}

/** Compose-рендер того же виджета. */
internal class CatalogShowcaseComposer : ChatWidgetComposer {

    @Composable
    override fun Content(message: WidgetMessage) {
        val limit = message.payload["limit"]?.toIntOrNull() ?: DEFAULT_LIMIT
        var products by remember { mutableStateOf<List<Product>?>(null) }
        LaunchedEffect(limit) {
            products = api<CatalogFeatureApi>().productRepository().getProducts().take(limit)
        }

        val context = LocalContext.current
        Column {
            Text("Витрина каталога", style = MaterialTheme.typography.labelSmall)
            when (val loaded = products) {
                null -> Text("Загрузка…", style = MaterialTheme.typography.bodyMedium)
                else -> loaded.forEach { product ->
                    Text("• ${product.title} — ${product.price} ₽", style = MaterialTheme.typography.bodyMedium)
                }
            }
            TextButton(onClick = {
                context.findFragmentActivity()?.let {
                    api<CatalogFeatureApi>().launcher().launch(it.supportFragmentManager)
                }
            }) {
                Text("В каталог")
            }
        }
    }
}

private const val DEFAULT_LIMIT = 3
