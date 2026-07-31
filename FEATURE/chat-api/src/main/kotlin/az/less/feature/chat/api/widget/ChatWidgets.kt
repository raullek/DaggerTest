package az.less.feature.chat.api.widget

import javax.inject.Qualifier

/**
 * Шов расширения чата из фич. Помечает `@IntoMap`-вклады виджетов (XML-фабрики и Compose-рендеры),
 * агрегируемые Dagger'ом в `AppComponent` — единственном месте, видящем все impl-модули.
 * Собранные `@ChatWidgets Map` прокидываются в `ChatComponent` через `@BindsInstance`
 * (тот же шов, что `@FeatureWidgets` у workflow).
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ChatWidgets
