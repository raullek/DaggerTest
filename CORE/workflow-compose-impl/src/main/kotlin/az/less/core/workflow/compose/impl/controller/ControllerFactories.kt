package az.less.core.workflow.compose.impl.controller

import az.less.core.workflow.compose.api.model.FieldType
import az.less.core.workflow.compose.api.widget.FieldControllerFactory

/**
 * Дефолтные фабрики контроллеров по типу поля. SELECT/RADIO → [SelectFieldController] (знает
 * справочник, looking-сторона стратегий), остальные → [DefaultFieldController]. Неизвестный тип
 * получает дефолтную фабрику (read-only через сам контроллер).
 */
object DefaultControllerFactories {

    private val selectFactory = FieldControllerFactory { field, references, formatters ->
        SelectFieldController(field, references, formatters)
    }

    private val defaultFactory = FieldControllerFactory { field, _, formatters ->
        DefaultFieldController(field, formatters)
    }

    val byType: Map<FieldType, FieldControllerFactory> = mapOf(
        FieldType.SELECT to selectFactory,
        FieldType.RADIO to selectFactory,
        FieldType.TEXT to defaultFactory,
        FieldType.INTEGER to defaultFactory,
        FieldType.DECIMAL to defaultFactory,
        FieldType.MONEY to defaultFactory,
        FieldType.DATE to defaultFactory,
        FieldType.PHONE to defaultFactory,
        FieldType.CHECKBOX to defaultFactory,
        FieldType.SWITCH to defaultFactory,
        FieldType.UNKNOWN to defaultFactory,
    )

    val default: FieldControllerFactory = defaultFactory
}
