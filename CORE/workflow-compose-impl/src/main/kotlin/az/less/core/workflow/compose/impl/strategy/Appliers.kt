package az.less.core.workflow.compose.impl.strategy

import az.less.core.workflow.compose.api.model.WfProperties
import az.less.core.workflow.compose.api.protocol.MutableDescriptionProtocol
import az.less.core.workflow.compose.api.protocol.MutableStyleProtocol
import az.less.core.workflow.compose.api.protocol.MutableVisibilityProtocol
import az.less.core.workflow.compose.api.protocol.ReferencesProtocol
import az.less.core.workflow.compose.api.protocol.ValueProtocol
import az.less.core.workflow.compose.api.strategy.StrategyApplier

/**
 * `updateDescription` — берёт description/style выбранного
 * элемента справочника у `looking` (SELECT/RADIO) и пишет их в `lookUp` (любое поле с
 * description/style). Так выбор типа реактивно меняет подсказку соседнего поля.
 */
class DescriptionAndIconStrategyApplier :
    StrategyApplier<ReferencesProtocol, MutableDescriptionProtocol> {

    override val lookingProtocols = listOf(ReferencesProtocol::class.java, ValueProtocol::class.java)
    override val lookUpProtocols = listOf(MutableDescriptionProtocol::class.java)

    override fun onApply(
        looking: ReferencesProtocol,
        lookUp: MutableDescriptionProtocol,
        config: WfProperties,
    ) {
        val item = looking.selectedReferenceItem() ?: return
        lookUp.setDescription(item.description)
        if (lookUp is MutableStyleProtocol && item.style != null) {
            lookUp.setStyle(item.style)
        }
    }
}

/**
 * `toggleVisibility` — показывает/прячет `lookUp` по значению `looking`. `config.hideWhen` = значение,
 * при котором цель скрывается (иначе показывается). Демонстрирует server-driven условный рендер.
 */
class VisibilityStrategyApplier :
    StrategyApplier<ValueProtocol, MutableVisibilityProtocol> {

    override val lookingProtocols = listOf(ValueProtocol::class.java)
    override val lookUpProtocols = listOf(MutableVisibilityProtocol::class.java)

    override fun onApply(
        looking: ValueProtocol,
        lookUp: MutableVisibilityProtocol,
        config: WfProperties,
    ) {
        // Пока тип не выбран — не трогаем видимость (поле остаётся скрытым по серверному дефолту).
        if (looking.value.isEmpty()) return
        val hideWhen = config.string("hideWhen")
        val showWhen = config.string("showWhen")
        val visible = when {
            hideWhen != null -> looking.value != hideWhen
            showWhen != null -> looking.value == showWhen
            else -> true
        }
        lookUp.setVisible(visible)
    }
}
