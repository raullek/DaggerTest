package az.less.core.workflow.compose.api.strategy

import az.less.core.workflow.compose.api.model.WfProperties
import az.less.core.workflow.compose.api.protocol.StrategyProtocol

/**
 * Типизированная связь двух виджетов через protocol-интерфейсы — ядро межвиджетной реактивности.
 *
 * Объявляет, какие протоколы требуются у `looking` (наблюдаемого) и `lookUp` (целевого) сторон.
 * StrategyResolver проверяет `is`-совместимость СТРУКТУРНО (не зная классов), кастует и зовёт
 * [onApply]. Так поведение «выбор в одном поле меняет другое» не привязано к конкретным виджетам.
 *
 * @param Looking тип протокола(ов) наблюдаемой стороны.
 * @param LookUp тип протокола(ов) целевой стороны.
 */
interface StrategyApplier<Looking : StrategyProtocol, LookUp : StrategyProtocol> {

    /** Протоколы, которые ОБЯЗАН реализовывать `looking`-контроллер. */
    val lookingProtocols: List<Class<*>>

    /** Протоколы, которые ОБЯЗАН реализовывать `lookUp`-контроллер. */
    val lookUpProtocols: List<Class<*>>

    /** Применить эффект: прочитать состояние `looking`, записать в `lookUp`. */
    fun onApply(looking: Looking, lookUp: LookUp, config: WfProperties)
}

/**
 * Фабрика применителей по строковому `strategyType`.
 * Реализация свитчится по типу; неизвестный тип → null (стратегия игнорируется).
 */
interface StrategyFactory {
    fun create(type: String): StrategyApplier<*, *>?
}
