package az.less.feature.profile.impl.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
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
import az.less.feature.chat.api.model.WidgetMessage
import az.less.feature.chat.api.widget.ChatWidgetComposer
import az.less.feature.chat.api.widget.ChatWidgetViewHolder
import az.less.feature.chat.api.widget.ChatWidgetViewHolderFactory
import az.less.feature.chat.api.widget.findFragmentActivity
import az.less.feature.profile.api.Profile
import az.less.feature.profile.api.ProfileFeatureApi
import az.less.feature.profile.impl.R
import kotlinx.coroutines.launch

/**
 * Виджет «карточка профиля» для чата — вклад фичи profile в оба рендера.
 * ДАННЫЕ агрегируются из СВОЕЙ фичи: репозиторий берём лениво через DI-фасад —
 * граф чата о графе профиля ничего не знает.
 */

/** XML-рендер: фабрика вьюхолдера. */
internal class ProfileCardViewHolderFactory : ChatWidgetViewHolderFactory {

    override fun create(parent: ViewGroup): ChatWidgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_widget_profile, parent, false)
        return object : ChatWidgetViewHolder(view) {
            private val name = view.findViewById<TextView>(R.id.name)
            private val email = view.findViewById<TextView>(R.id.email)
            private val open = view.findViewById<Button>(R.id.open)

            override fun bind(message: WidgetMessage) {
                name.text = "Загрузка…"
                email.text = ""
                // Подгружаем модель СВОЕЙ фичи, когда вьюха попала в дерево (есть LifecycleOwner).
                itemView.doOnAttach {
                    val owner = itemView.findViewTreeLifecycleOwner() ?: return@doOnAttach
                    owner.lifecycleScope.launch {
                        val profile = api<ProfileFeatureApi>().profileRepository().getProfile()
                        name.text = profile.name
                        email.text = profile.email
                    }
                }
                open.setOnClickListener { view ->
                    view.context.findFragmentActivity()?.let {
                        api<ProfileFeatureApi>().launcher().launch(it.supportFragmentManager)
                    }
                }
            }
        }
    }
}

/** Compose-рендер того же виджета (параллельное поколение, тот же ключ в своей карте). */
internal class ProfileCardComposer : ChatWidgetComposer {

    @Composable
    override fun Content(message: WidgetMessage) {
        var profile by remember { mutableStateOf<Profile?>(null) }
        LaunchedEffect(Unit) {
            profile = api<ProfileFeatureApi>().profileRepository().getProfile()
        }

        val context = LocalContext.current
        Column {
            Text("Профиль", style = MaterialTheme.typography.labelSmall)
            Text(profile?.name ?: "Загрузка…", style = MaterialTheme.typography.titleMedium)
            Text(profile?.email.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = {
                context.findFragmentActivity()?.let {
                    api<ProfileFeatureApi>().launcher().launch(it.supportFragmentManager)
                }
            }) {
                Text("Открыть профиль")
            }
        }
    }
}
