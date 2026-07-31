package az.less.core.deeplink.api

/**
 * Билдер обработчика из шагов
 * (`addStep`/`addAllSteps`/`setFailedResultHandler`/`build`).
 *
 * Сам прогон шагов вынесен в [DeeplinkStepsRunner] (реализация в :impl —
 * последовательная, с обрывом на первом Failed), чтобы билдер из :api не тащил
 * детали выполнения. Фича пишет: `factory.builder().addStep(...).build()`.
 */
class DeeplinkHandlerBuilder(
    private val runner: DeeplinkStepsRunner,
) {
    private val steps = mutableListOf<DeeplinkStep>()
    private var failedResultHandler: FailedResultHandler = FailedResultHandler.Noop
    private var enabled: Boolean = true

    fun addStep(step: DeeplinkStep): DeeplinkHandlerBuilder = apply { steps += step }

    fun addAllSteps(steps: List<DeeplinkStep>): DeeplinkHandlerBuilder = apply { this.steps += steps }

    fun setFailedResultHandler(handler: FailedResultHandler): DeeplinkHandlerBuilder =
        apply { failedResultHandler = handler }

    fun setEnabled(isEnabled: Boolean): DeeplinkHandlerBuilder = apply { enabled = isEnabled }

    fun build(): DeeplinkHandler = object : DeeplinkHandler {
        override val isEnabled: Boolean get() = enabled
        override suspend fun handle(deeplinkUri: DeeplinkUri): HandlingResult =
            runner.run(steps.toList(), failedResultHandler, deeplinkUri)
    }
}

/**
 * Выполняет список шагов. Реализация (AndroidDeeplinkStepsRunner в :impl) гоняет
 * их последовательно, обрывая на первом Failed, и при провале без само-обработки
 * зовёт [FailedResultHandler]. Вынесено в интерфейс ради тестируемости.
 */
interface DeeplinkStepsRunner {
    suspend fun run(
        steps: List<DeeplinkStep>,
        failedResultHandler: FailedResultHandler,
        deeplinkUri: DeeplinkUri,
    ): HandlingResult
}

/**
 * Обработчик финального провала цепочки (показать диалог «что-то пошло не так»).
 */
fun interface FailedResultHandler {
    suspend fun handleFailedResult(deeplinkUri: DeeplinkUri, result: HandlingResult.Failed)

    companion object {
        val Noop = FailedResultHandler { _, _ -> }
    }
}
