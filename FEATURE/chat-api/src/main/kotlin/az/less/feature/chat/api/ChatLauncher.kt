package az.less.feature.chat.api

import androidx.fragment.app.FragmentManager

/**
 * Точка входа в чат. Два рендера одного экрана поверх ОБЩЕГО домена
 * (агрегация виджетов одна, отличается только слой отображения):
 * - [launchXml] — классический RecyclerView + вьюхолдеры;
 * - [launchCompose] — Jetpack Compose (как BDUI-поколение).
 */
interface ChatLauncher {
    fun launchXml(fragmentManager: FragmentManager)
    fun launchCompose(fragmentManager: FragmentManager)
}
