package az.less.core.workflow.compose.impl.strategy

import android.util.Log
import az.less.core.workflow.compose.api.model.StrategyDescriptor
import az.less.core.workflow.compose.api.protocol.StrategyProtocol
import az.less.core.workflow.compose.api.strategy.StrategyApplier
import az.less.core.workflow.compose.api.strategy.StrategyFactory
import az.less.core.workflow.compose.api.widget.WidgetScope

/**
 * Сшивает межвиджетные стратегии экрана.
 *
 * Отложенная сборка: стратегия привязывается, только когда **оба** контроллера (looking/lookUp)
 * существуют и протоколы совместимы; несовместимые/неполные уходят в pending и доезжают при
 * [onControllerReady] (на случай динамической регистрации). После привязки: применяется один раз
 * (начальное состояние) и переподписывается на изменения `looking` (ввод пользователя).
 *
 * Контроллеры адресуются по строковым ключам структурно через `is`-проверку протоколов — резолвер не
 * знает их классов.
 */
class StrategyResolver(
    private val factory: StrategyFactory,
    private val scope: WidgetScope,
) {

    private val pending = mutableListOf<StrategyDescriptor>()

    fun resolve(strategies: List<StrategyDescriptor>) {
        pending.clear()
        strategies.forEach { descriptor ->
            if (!tryBind(descriptor)) pending += descriptor
        }
    }

    /** Повторная попытка для отложенных стратегий (если контроллер появился позже). */
    fun onControllerReady() {
        if (pending.isEmpty()) return
        val iterator = pending.iterator()
        while (iterator.hasNext()) {
            if (tryBind(iterator.next())) iterator.remove()
        }
    }

    private fun tryBind(descriptor: StrategyDescriptor): Boolean {
        val applier = factory.create(descriptor.type) ?: run {
            Log.w(TAG, "Неизвестная стратегия: ${descriptor.type}")
            return true // нечего ждать — считаем «разрешённой» (пропускаем)
        }
        val looking = scope.controller(descriptor.lookingKey) ?: return false
        val lookUp = scope.controller(descriptor.lookUpKey) ?: return false

        if (!compatible(applier, looking, lookUp)) {
            Log.w(
                TAG,
                "Стратегия ${descriptor.type} несовместима: " +
                    "looking=${descriptor.lookingKey}, lookUp=${descriptor.lookUpKey}",
            )
            return true // протоколы не сойдутся и позже — не держим в pending
        }

        val apply = { applyUnchecked(applier, looking, lookUp, descriptor) }
        apply() // начальное применение
        looking.observeChanges(apply) // реакция на ввод пользователя
        return true
    }

    private fun compatible(
        applier: StrategyApplier<*, *>,
        looking: StrategyProtocol,
        lookUp: StrategyProtocol,
    ): Boolean =
        applier.lookingProtocols.all { it.isInstance(looking) } &&
            applier.lookUpProtocols.all { it.isInstance(lookUp) }

    @Suppress("UNCHECKED_CAST")
    private fun applyUnchecked(
        applier: StrategyApplier<*, *>,
        looking: StrategyProtocol,
        lookUp: StrategyProtocol,
        descriptor: StrategyDescriptor,
    ) {
        (applier as StrategyApplier<StrategyProtocol, StrategyProtocol>)
            .onApply(looking, lookUp, descriptor.config)
    }

    private companion object {
        const val TAG = "BduiCompose"
    }
}
