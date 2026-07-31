package az.less.core.workflow.compose.impl.di

import az.less.core.workflow.compose.api.validation.ValidatorCompiler
import az.less.core.workflow.compose.impl.validation.MaxLengthCompiler
import az.less.core.workflow.compose.impl.validation.MaxValueCompiler
import az.less.core.workflow.compose.impl.validation.MinLengthCompiler
import az.less.core.workflow.compose.impl.validation.MinValueCompiler
import az.less.core.workflow.compose.impl.validation.RegexpCompiler
import az.less.core.workflow.compose.impl.validation.RequiredCompiler
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

/**
 * Реестр компиляторов валидаторов BDUI-Compose: `Map<validator.type, ValidatorCompiler>`. Включается
 * в `app/AppHolderModule`; фича может зарегистрировать новый тип валидатора. Расширяемо.
 */
@Module
interface WorkflowComposeValidatorsModule {

    @Binds @IntoMap @StringKey("REQUIRED")
    fun required(impl: RequiredCompiler): ValidatorCompiler

    @Binds @IntoMap @StringKey("MIN_LENGTH")
    fun minLength(impl: MinLengthCompiler): ValidatorCompiler

    @Binds @IntoMap @StringKey("MAX_LENGTH")
    fun maxLength(impl: MaxLengthCompiler): ValidatorCompiler

    @Binds @IntoMap @StringKey("REGEXP")
    fun regexp(impl: RegexpCompiler): ValidatorCompiler

    @Binds @IntoMap @StringKey("MIN_VALUE")
    fun minValue(impl: MinValueCompiler): ValidatorCompiler

    @Binds @IntoMap @StringKey("MAX_VALUE")
    fun maxValue(impl: MaxValueCompiler): ValidatorCompiler
}
