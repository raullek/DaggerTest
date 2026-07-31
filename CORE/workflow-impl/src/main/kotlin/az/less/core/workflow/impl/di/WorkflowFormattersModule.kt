package az.less.core.workflow.impl.di

import az.less.core.workflow.api.format.FormatterRegistry
import az.less.core.workflow.api.format.ValueFormatter
import az.less.core.workflow.impl.format.DateValueFormatter
import az.less.core.workflow.impl.format.DefaultFormatterRegistry
import az.less.core.workflow.impl.format.MoneyValueFormatter
import az.less.core.workflow.impl.format.PhoneValueFormatter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

/**
 * Реестр форматтеров: дефолты ядра в `Map<String, ValueFormatter>` (ключ — имя FieldType).
 * Включается в `app/AppHolderModule` — фича может добавить свой `@IntoMap` рядом. Расширяемо.
 */
@Module
interface WorkflowFormattersModule {

    @Binds
    fun bindFormatterRegistry(impl: DefaultFormatterRegistry): FormatterRegistry

    companion object {
        @Provides @IntoMap @StringKey("MONEY")
        fun money(): ValueFormatter = MoneyValueFormatter

        @Provides @IntoMap @StringKey("DATE")
        fun date(): ValueFormatter = DateValueFormatter

        @Provides @IntoMap @StringKey("PHONE")
        fun phone(): ValueFormatter = PhoneValueFormatter
    }
}
