package az.less.feature.chat.impl.navigation

import androidx.fragment.app.FragmentManager
import az.less.feature.chat.api.ChatLauncher
import az.less.feature.chat.impl.presentation.compose.ChatComposeFragment
import az.less.feature.chat.impl.presentation.xml.ChatFragment
import javax.inject.Inject

/** Вход в чат: два фрагмента-рендера поверх одного домена (@PerFeature-репозиторий общий). */
class ChatLauncherImpl @Inject constructor() : ChatLauncher {

    override fun launchXml(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, ChatFragment())
            .addToBackStack("chat-xml")
            .commit()
    }

    override fun launchCompose(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, ChatComposeFragment())
            .addToBackStack("chat-compose")
            .commit()
    }
}
