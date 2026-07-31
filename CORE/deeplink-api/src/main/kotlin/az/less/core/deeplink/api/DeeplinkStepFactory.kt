package az.less.core.deeplink.api

/**
 * То, что фича получает из ядра, чтобы собрать свой обработчик, ничего не зная о
 * внутренней реализации прогона шагов.
 *
 * Использование в фиче (внутри `@IntoSet DeeplinkHandlerEntry`):
 * ```
 * val factory = api<DeeplinkFeatureApi>().stepFactory()
 * factory.handlerBuilder()
 *     .addStep(LaunchFeatureDeeplinkStep(factory.currentActivityProvider()) { fm ->
 *         api<CatalogFeatureApi>().launcher().launch(fm)
 *     })
 *     .build()
 * ```
 */
interface DeeplinkStepFactory {
    /** Новый билдер обработчика с уже встроенным раннером шагов. */
    fun handlerBuilder(): DeeplinkHandlerBuilder

    /** Провайдер текущей Activity — для навигационных шагов. */
    fun currentActivityProvider(): CurrentActivityProvider
}
