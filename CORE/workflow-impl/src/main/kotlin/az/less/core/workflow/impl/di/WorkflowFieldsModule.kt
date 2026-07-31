package az.less.core.workflow.impl.di

import android.text.InputType
import az.less.core.designsystem.DsComponent
import az.less.core.workflow.api.format.FormatterRegistry
import az.less.core.workflow.api.widget.FieldRenderer
import az.less.core.workflow.impl.render.CheckboxFieldRenderer
import az.less.core.workflow.impl.render.InputFieldRenderer
import az.less.core.workflow.impl.render.RadioFieldRenderer
import az.less.core.workflow.impl.render.SelectFieldRenderer
import az.less.core.workflow.impl.render.SwitchFieldRenderer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

/**
 * Реестр рендереров полей: `Map<String, FieldRenderer>` (ключ — имя FieldType). Включается в
 * `app/AppHolderModule`; фича может добавить рендерер нового типа поля. Расширяемо.
 */
@Module
interface WorkflowFieldsModule {

    @Binds @IntoMap @StringKey("SELECT")
    fun select(impl: SelectFieldRenderer): FieldRenderer

    @Binds @IntoMap @StringKey("RADIO")
    fun radio(impl: RadioFieldRenderer): FieldRenderer

    @Binds @IntoMap @StringKey("CHECKBOX")
    fun checkbox(impl: CheckboxFieldRenderer): FieldRenderer

    @Binds @IntoMap @StringKey("SWITCH")
    fun switchField(impl: SwitchFieldRenderer): FieldRenderer

    companion object {
        @Provides @IntoMap @StringKey("TEXT")
        fun text(f: FormatterRegistry): FieldRenderer =
            InputFieldRenderer(f, InputType.TYPE_CLASS_TEXT, DsComponent.TEXT_FIELD)

        @Provides @IntoMap @StringKey("INTEGER")
        fun integer(f: FormatterRegistry): FieldRenderer =
            InputFieldRenderer(f, InputType.TYPE_CLASS_NUMBER, DsComponent.TEXT_FIELD)

        @Provides @IntoMap @StringKey("DECIMAL")
        fun decimal(f: FormatterRegistry): FieldRenderer = InputFieldRenderer(
            f, InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL, DsComponent.TEXT_FIELD,
        )

        @Provides @IntoMap @StringKey("MONEY")
        fun money(f: FormatterRegistry): FieldRenderer = InputFieldRenderer(
            f, InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL, DsComponent.MONEY_FIELD,
        )

        @Provides @IntoMap @StringKey("PHONE")
        fun phone(f: FormatterRegistry): FieldRenderer =
            InputFieldRenderer(f, InputType.TYPE_CLASS_PHONE, DsComponent.TEXT_FIELD)

        @Provides @IntoMap @StringKey("DATE")
        fun date(f: FormatterRegistry): FieldRenderer = InputFieldRenderer(
            f, InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE, DsComponent.DATE_FIELD,
        )
    }
}
