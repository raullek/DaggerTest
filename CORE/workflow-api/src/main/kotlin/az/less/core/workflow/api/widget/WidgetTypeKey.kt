package az.less.core.workflow.api.widget

import dagger.MapKey

/**
 * Ключ мультибиндинга для регистрации виджета по его строковому типу
 * через Dagger-`@IntoMap` без кодогена.
 *
 * Любой модуль (ядро или фича) регистрирует свой [WidgetViewHolderFactory] так:
 * ```
 * @Provides @IntoMap @WidgetTypeKey("TRANSFER_RECEIPT")
 * fun provide(...): WidgetViewHolderFactory = ...
 * ```
 * Dagger мёржит все вклады в `Map<String, WidgetViewHolderFactory>` (агрегация в AppComponent).
 */
@MapKey
annotation class WidgetTypeKey(val value: String)
