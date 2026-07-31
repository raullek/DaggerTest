package az.less.feature.transfer.impl.di

import az.less.core.workflow.api.widget.FeatureWidgets
import az.less.core.workflow.api.widget.WidgetTypeKey
import az.less.core.workflow.api.widget.WidgetViewHolderFactory
import az.less.feature.transfer.impl.widget.TransferReceiptViewHolderFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Вклад фичи в РЕЕСТР виджетов ядра: регистрирует кастомный `TRANSFER_RECEIPT` через
 * `@IntoMap @FeatureWidgets @WidgetTypeKey`. Модуль включается в `app/AppHolderModule` — Dagger
 * собирает в AppComponent `@FeatureWidgets Map` из вкладов всех фич и прокидывает её в граф workflow,
 * где [WorkflowComponent] сливает её с ядровыми виджетами. Ядро кода этого виджета не содержит —
 * чистая точка расширения.
 */
@Module
interface TransferWidgetsModule {

    @Binds
    @IntoMap
    @FeatureWidgets
    @WidgetTypeKey("TRANSFER_RECEIPT")
    fun receipt(impl: TransferReceiptViewHolderFactory): WidgetViewHolderFactory
}
