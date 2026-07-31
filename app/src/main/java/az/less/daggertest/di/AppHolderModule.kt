package az.less.daggertest.di

import az.less.core.network.impl.NetworkCoreHolderModule
import az.less.core.workflow.impl.di.WorkflowHolderModule
import az.less.core.workflow.compose.impl.di.WorkflowComposeHolderModule
import az.less.daggertest.di.chat.ChatHolderModule
import az.less.feature.catalog.impl.di.CatalogHolderModule
import az.less.feature.profile.impl.di.ProfileHolderModule
import az.less.feature.settings.impl.di.SettingsHolderModule
import az.less.feature.transfer.impl.di.TransferHolderModule
import az.less.feature.transfer.impl.di.TransferWidgetsModule
import dagger.Module

/**
 * Собирает HolderModule'ы всех модулей. Каждый кладёт свой FeatureHolder в общую
 * `Map<Class, FeatureHolder>` через @IntoMap @ClassKey.
 *
 * Внутренние реестры движков SDUI (поля/форматтеры/валидаторы/ядровые виджеты) НЕ агрегируются здесь
 * — они строятся внутри `WorkflowComponent`/`WorkflowComposeComponent`. В корне остаётся только то,
 * что принципиально требует обзора всех фич: вклады виджетов от фич (`@FeatureWidgets`, напр.
 * [TransferWidgetsModule]). Так корневой компонент не пухнет с ростом числа модулей.
 *
 * Обработка диплинков вынесена в отдельный [AppDeeplinkModule].
 */
@Module(
    includes = [
        NetworkCoreHolderModule::class,
        // Ядро server-driven UI: только холдер + @Multibinds-шов для вкладов виджетов от фич.
        WorkflowHolderModule::class,
        // BDUI-движок на Compose: только холдер + @Multibinds-шов для вкладов виджетов от фич.
        WorkflowComposeHolderModule::class,
        ProfileHolderModule::class,
        CatalogHolderModule::class,
        SettingsHolderModule::class,
        // Фича перевода: её холдер + вклад кастомного виджета TRANSFER_RECEIPT (@FeatureWidgets).
        TransferHolderModule::class,
        TransferWidgetsModule::class,
        // Чат: в корне ТОЛЬКО холдер — вклады фич собирает ChatComponent (app/di/chat)
        // своим CompoundChatWidgetsModule (компонент агрегатора в app-модуле).
        ChatHolderModule::class,
    ],
)
interface AppHolderModule
