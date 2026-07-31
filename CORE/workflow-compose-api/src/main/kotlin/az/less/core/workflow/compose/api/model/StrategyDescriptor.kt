package az.less.core.workflow.compose.api.model

/**
 * Серверное описание межвиджетной стратегии. Связывает два контроллера:
 *
 * - [lookingKey] — «наблюдаемый» виджет/поле (источник изменений);
 * - [lookUpKey] — «целевой» виджет/поле (получатель эффекта).
 *
 * [type] выбирает [az.less.core.workflow.compose.api.strategy.StrategyApplier] в StrategyFactory.
 * [config] — произвольные параметры стратегии. Стратегия срабатывает отложенно: только когда оба
 * контроллера созданы и references загружены (см. StrategyResolver).
 */
data class StrategyDescriptor(
    val type: String,
    val lookingKey: String,
    val lookUpKey: String,
    val config: WfProperties = WfProperties.EMPTY,
)
