package az.less.core.workflow.compose.api.protocol

import az.less.core.workflow.compose.api.model.WfReferenceItem

/**
 * Protocol/capability-интерфейсы. Контроллер виджета реализует
 * подмножество протоколов, которое поддерживает; [az.less.core.workflow.compose.api.strategy.StrategyApplier]
 * требует определённые протоколы у `looking`/`lookUp` сторон и работает СТРУКТУРНО — через
 * проверку `is`, не зная конкретных классов. Это и есть декаплинг кросс-виджетного поведения.
 *
 * Mutable-варианты пишут «программно» (канал модели), не трогая UI-канал ввода — против echo-петель.
 */

/** Маркер: «этот контроллер участвует в стратегиях». */
interface StrategyProtocol

/** Имеет текущее server-значение. */
interface ValueProtocol : StrategyProtocol {
    val value: String
}

/** Значение можно задать программно (модельный канал). */
interface MutableValueProtocol : ValueProtocol {
    fun setValueFromModel(value: String)
}

/** Имеет подпись-описание под/над полем. */
interface DescriptionProtocol : StrategyProtocol {
    val description: String?
}

interface MutableDescriptionProtocol : DescriptionProtocol {
    fun setDescription(description: String?)
}

/** Имеет визуальный стиль (имя варианта компонента). */
interface StyleProtocol : StrategyProtocol {
    val style: String?
}

interface MutableStyleProtocol : StyleProtocol {
    fun setStyle(style: String?)
}

/** Знает свой справочник и выбранный из него элемент (источник description/style для стратегий). */
interface ReferencesProtocol : StrategyProtocol {
    fun selectedReferenceItem(): WfReferenceItem?
}

/** Видимость виджета можно менять реактивно (server-driven скрытие/показ). */
interface VisibilityProtocol : StrategyProtocol {
    val visible: Boolean
}

interface MutableVisibilityProtocol : VisibilityProtocol {
    fun setVisible(visible: Boolean)
}

/** Доступность ввода. */
interface ReadonlyProtocol : StrategyProtocol {
    val readonly: Boolean
}

interface MutableReadonlyProtocol : ReadonlyProtocol {
    fun setReadonly(readonly: Boolean)
}

/** Может показать ошибку под собой. */
interface ErrorProtocol : StrategyProtocol {
    val error: String?
    fun setError(error: String?)
}

/** Имеет заголовок. */
interface TitleProtocol : StrategyProtocol {
    val title: String
}
