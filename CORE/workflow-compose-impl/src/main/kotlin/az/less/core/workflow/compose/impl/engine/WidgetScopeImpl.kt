package az.less.core.workflow.compose.impl.engine

import az.less.core.workflow.compose.api.format.FormatterRegistry
import az.less.core.workflow.compose.api.model.WfReferences
import az.less.core.workflow.compose.api.model.WfScreen
import az.less.core.workflow.compose.api.widget.FieldController
import az.less.core.workflow.compose.api.widget.Reflector
import az.less.core.workflow.compose.api.widget.WidgetScope

/**
 * Реестр контроллеров полей одного экрана. Создаётся раз на экран; держит
 * контроллеры по `field.id` в порядке появления. По нему StrategyResolver находит looking/lookUp.
 */
class WidgetScopeImpl(
    private val controllers: LinkedHashMap<String, FieldController>,
) : WidgetScope {

    override fun controller(fieldId: String): FieldController? = controllers[fieldId]

    override fun controllers(): List<FieldController> = controllers.values.toList()

    override fun retrieveData(): Map<String, String> =
        controllers.values
            .filter { it.visible }
            .associate { it.key to it.collect() }

    override fun validateAll(): Boolean {
        var allValid = true
        // Прогоняем ВСЕ поля (а не до первого провала), чтобы подсветить все ошибки сразу.
        controllers.values.forEach { controller ->
            if (!controller.validate()) allValid = false
        }
        return allValid
    }

    companion object {
        /**
         * Собрать scope экрана: на каждое поле — контроллер из фабрики по `field.type` (реестр в
         * [Reflector]). Идентичные ключи перетираются последним (id полей уникальны на экране).
         */
        fun build(
            screen: WfScreen,
            references: WfReferences,
            reflector: Reflector,
            formatters: FormatterRegistry,
        ): WidgetScopeImpl {
            val controllers = LinkedHashMap<String, FieldController>()
            screen.fields.forEach { field ->
                val controller = reflector.fieldControllerFactory(field.type)
                    .create(field, references, formatters)
                controllers[field.id] = controller
            }
            return WidgetScopeImpl(controllers)
        }
    }
}
