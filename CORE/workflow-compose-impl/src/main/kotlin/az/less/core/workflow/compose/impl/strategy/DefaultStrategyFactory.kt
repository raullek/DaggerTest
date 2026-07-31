package az.less.core.workflow.compose.impl.strategy

import az.less.core.workflow.compose.api.strategy.StrategyApplier
import az.less.core.workflow.compose.api.strategy.StrategyFactory
import javax.inject.Inject

/**
 * Фабрика применителей по строковому `strategyType`. Новый тип
 * стратегии добавляется веткой здесь (или, в продакшене, мультибиндингом). Неизвестный → null.
 */
class DefaultStrategyFactory @Inject constructor() : StrategyFactory {
    override fun create(type: String): StrategyApplier<*, *>? = when (type) {
        "updateDescription" -> DescriptionAndIconStrategyApplier()
        "toggleVisibility" -> VisibilityStrategyApplier()
        else -> null
    }
}
