package az.less.core.workflow.compose.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import az.less.core.designsystem.compose.theme.DsTheme
import az.less.core.workflow.compose.api.engine.BduiStateMachine
import az.less.core.workflow.compose.api.engine.WorkflowState
import az.less.core.workflow.compose.api.format.FormatterRegistry
import az.less.core.workflow.compose.api.model.WfEvent
import az.less.core.workflow.compose.api.model.WorkflowResponse
import az.less.core.workflow.compose.api.strategy.StrategyFactory
import az.less.core.workflow.compose.api.widget.Reflector
import az.less.core.workflow.compose.api.widget.ScreenItem
import az.less.core.workflow.compose.api.widget.WidgetScope
import az.less.core.workflow.compose.api.widget.WorkflowInteraction
import az.less.core.workflow.compose.impl.engine.WidgetScopeImpl
import az.less.core.workflow.compose.impl.strategy.StrategyResolver

/**
 * Корневой композабл BDUI-хоста. Подписывается на [BduiStateMachine.state] и рисует экран в **три
 * региона** (header/main/footer): закреплённый верх (степпер+заголовок+header-виджеты),
 * скроллируемое тело, закреплённый низ (footer-виджеты + кнопки-события).
 *
 * На каждый экран собирает [WidgetScope] (контроллеры полей) и сшивает межвиджетные стратегии через
 * [StrategyResolver] — всё в `remember(response)`, чтобы пережить рекомпозиции, но пересоздаться на
 * новом экране.
 */
@Composable
fun WorkflowHost(
    stateMachine: BduiStateMachine,
    reflector: Reflector,
    formatters: FormatterRegistry,
    strategyFactory: StrategyFactory,
    flow: String,
    onExit: () -> Unit,
    onMessage: (String) -> Unit,
) {
    LaunchedEffect(flow) { stateMachine.start(flow) }

    LaunchedEffect(stateMachine) {
        stateMachine.messages.collect { message ->
            onMessage(message.text)
            if (message.fatal) onExit()
        }
    }

    val state by stateMachine.state.collectAsStateWithLifecycle()

    when (val s = state) {
        is WorkflowState.Idle, is WorkflowState.Loading -> LoadingScreen()
        is WorkflowState.Failed -> ErrorScreen(s.message)
        is WorkflowState.Finished -> LaunchedEffect(s) { onExit() }
        is WorkflowState.Screen -> ScreenContent(
            response = s.response,
            reflector = reflector,
            formatters = formatters,
            strategyFactory = strategyFactory,
            stateMachine = stateMachine,
        )
    }
}

@Composable
private fun ScreenContent(
    response: WorkflowResponse,
    reflector: Reflector,
    formatters: FormatterRegistry,
    strategyFactory: StrategyFactory,
    stateMachine: BduiStateMachine,
) {
    val screen = response.screen ?: return

    // Scope + стратегии собираются раз на экран (переживают рекомпозиции).
    val scope: WidgetScope = remember(response) {
        WidgetScopeImpl.build(screen, response.references, reflector, formatters).also { built ->
            StrategyResolver(strategyFactory, built).resolve(screen.strategies)
        }
    }

    val interaction = remember(response, scope) {
        object : WorkflowInteraction {
            override fun submit(event: WfEvent) {
                if (scope.validateAll()) {
                    stateMachine.sendEvent(event.name, scope.retrieveData())
                }
            }

            override fun rollback() = stateMachine.rollback()
        }
    }

    val step = screen.properties.int("step")
    val steps = screen.properties.int("steps")

    CompositionLocalProvider(LocalWorkflowReflector provides reflector) {
        Column(Modifier.fillMaxSize().padding(DsTheme.dimens.spaceL)) {
            // --- закреплённый верх ---
            if (steps > 0) {
                Render(ScreenItem(ScreenItem.TYPE_STEPPER, scope, step = step, steps = steps), reflector, interaction)
            }
            Render(
                ScreenItem(ScreenItem.TYPE_HEADER, scope, title = screen.title, subtitle = screen.description),
                reflector,
                interaction,
            )
            screen.header.forEach { widget ->
                Render(item(widget, scope, response), reflector, interaction)
            }

            // --- скроллируемое тело ---
            Column(
                Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(DsTheme.dimens.spaceM),
            ) {
                screen.widgets.forEach { widget ->
                    Render(item(widget, scope, response), reflector, interaction)
                }
            }

            // --- закреплённый низ ---
            Column(verticalArrangement = Arrangement.spacedBy(DsTheme.dimens.spaceM)) {
                screen.footer.forEach { widget ->
                    Render(item(widget, scope, response), reflector, interaction)
                }
                response.events.filterNot { it.hidden }.forEach { event ->
                    Render(ScreenItem(ScreenItem.TYPE_EVENT, scope, event = event), reflector, interaction)
                }
            }
        }
    }
}

private fun item(
    widget: az.less.core.workflow.compose.api.model.WfWidget,
    scope: WidgetScope,
    response: WorkflowResponse,
) = ScreenItem(
    typeKey = widget.type,
    scope = scope,
    widget = widget,
    references = response.references,
)

@Composable
private fun Render(item: ScreenItem, reflector: Reflector, interaction: WorkflowInteraction) {
    reflector.widgetRenderer(item.typeKey)(item, interaction)
}

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(message: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(message, color = DsTheme.colors.error, style = MaterialTheme.typography.bodyLarge)
    }
}
