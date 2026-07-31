package az.less.daggertest.di.chat

import az.less.core.di.FeatureContainer
import az.less.core.di.FeatureHolder
import az.less.feature.chat.api.ChatFeatureApi
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Singleton

/**
 * Регистрирует холдер чата в общую `Map<Class, FeatureHolder>`. Это ЕДИНСТВЕННОЕ,
 * что чат добавляет в корневой AppComponent, — все агрегаты вкладов живут внутри
 * [ChatComponent] и корень не пухнет (в отличие от прежней схемы с @BindsInstance).
 */
@Module
interface ChatHolderModule {

    companion object {
        @Provides
        @Singleton
        @IntoMap
        @ClassKey(ChatFeatureApi::class)
        fun provideChatHolder(container: FeatureContainer): FeatureHolder<*> =
            ChatFeatureHolder(container)
    }
}
