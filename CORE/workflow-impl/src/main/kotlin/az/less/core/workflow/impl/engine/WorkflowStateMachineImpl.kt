package az.less.core.workflow.impl.engine

import az.less.core.workflow.api.check.WorkflowResultValidator
import az.less.core.workflow.api.engine.WorkflowRepository
import az.less.core.workflow.api.engine.WorkflowState
import az.less.core.workflow.api.engine.WorkflowStateMachine
import az.less.core.workflow.api.engine.WorkflowUserMessage
import az.less.core.workflow.api.model.Command
import az.less.core.workflow.api.model.DocumentAttributes
import az.less.core.workflow.api.model.WorkflowRequest
import az.less.core.workflow.api.model.WorkflowResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Движок выполнения флоу на корутинах: `suspend`/[CoroutineScope] и состояние
 * в [MutableStateFlow].
 *
 * Цикл: [start]/[sendEvent] → Loading → запрос через [WorkflowRepository] → разбор ошибок
 * валидатором → новое состояние (Screen/Finished/Failed). История экранов держится
 * локально для [rollback] (как «назад» без обращения к серверу).
 *
 * Короткоживущий: по одному инстансу на запуск флоу (см. `newStateMachine()`).
 */
class WorkflowStateMachineImpl @Inject constructor(
    private val repository: WorkflowRepository,
    private val resultValidator: WorkflowResultValidator,
) : WorkflowStateMachine {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow<WorkflowState>(WorkflowState.Idle)
    override val state = _state.asStateFlow()

    private val _messages = MutableSharedFlow<WorkflowUserMessage>(extraBufferCapacity = 16)
    override val messages = _messages.asSharedFlow()

    override var currentResponse: WorkflowResponse? = null
        private set

    private var flow: String = ""
    private val history = ArrayDeque<WorkflowResponse>()
    private var job: Job? = null

    init {
        // Валидатор разбирает ответ и шлёт ошибки сюда — каждая становится сообщением UI.
        resultValidator.setErrorHandler { msgs ->
            msgs.forEach { _messages.tryEmit(WorkflowUserMessage(it.text, fatal = false)) }
        }
        resultValidator.setFatalHandler { msgs ->
            msgs.forEach { _messages.tryEmit(WorkflowUserMessage(it.text, fatal = true)) }
        }
    }

    override fun start(flow: String, request: WorkflowRequest?) {
        this.flow = flow
        history.clear()
        val body = request ?: WorkflowRequest(DocumentAttributes(flow = flow))
        run(Command.START, eventName = null, request = body)
    }

    override fun sendEvent(eventName: String, fields: Map<String, String>, force: Boolean) {
        val current = currentResponse
        val document = DocumentAttributes(
            flow = flow,
            state = current?.state,
            documentId = current?.pid,
        )
        run(Command.EVENT, eventName = eventName, request = WorkflowRequest(document, fields))
    }

    override fun rollback() {
        // Локальный шаг назад: снимаем текущий экран, показываем предыдущий.
        if (history.size >= 2) {
            history.removeLast()
            val previous = history.last()
            currentResponse = previous
            _state.value = WorkflowState.Screen(previous)
        }
    }

    override fun reset() {
        job?.cancel()
        history.clear()
        currentResponse = null
        _state.value = WorkflowState.Idle
    }

    private fun run(command: Command, eventName: String?, request: WorkflowRequest) {
        job?.cancel()
        job = scope.launch {
            _state.value = WorkflowState.Loading
            try {
                val response = repository.doEvent(
                    command = command,
                    flow = flow,
                    pid = currentResponse?.pid,
                    eventName = eventName,
                    request = request,
                )

                // Информационные сообщения показываем всегда (например, на END).
                response.messages.filterNot { it.isError }
                    .forEach { _messages.tryEmit(WorkflowUserMessage(it.text, fatal = false)) }

                val success = resultValidator.validate(response)
                if (!success) {
                    // Ошибки уже разосланы обработчиками — остаёмся на текущем экране.
                    currentResponse?.let { _state.value = WorkflowState.Screen(it) }
                    return@launch
                }

                currentResponse = response
                if (response.isEnd) {
                    _state.value = WorkflowState.Finished(response.exitUri)
                } else {
                    history.addLast(response)
                    _state.value = WorkflowState.Screen(response)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = WorkflowState.Failed(e.message ?: "Ошибка обработки экрана")
            }
        }
    }
}
