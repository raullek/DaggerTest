package az.less.core.workflow.impl.logging

import android.util.Log
import az.less.core.workflow.api.engine.WorkflowLogger
import az.less.core.workflow.api.model.Command
import az.less.core.workflow.api.model.WorkflowRequest
import az.less.core.workflow.api.model.WorkflowResponse
import javax.inject.Inject

/**
 * Дефолтный логгер SDUI-экшенов в Logcat (тег `WorkflowSDUI`). Показывает на каждый экшн: команду,
 * флоу, текущий стейт, имя события и отправляемые `fields` (id → value), затем результат сервера.
 *
 * Фильтр в Logcat: `adb logcat -s WorkflowSDUI`.
 *
 * ВНИМАНИЕ: для боевого банка значения полей чувствительны (суммы/телефоны) — в проде логировать
 * только в debug-сборке либо маскировать значения. Здесь — полностью, ради наглядности демо.
 */
class AndroidWorkflowLogger @Inject constructor() : WorkflowLogger {

    override fun logAction(command: Command, flow: String, eventName: String?, request: WorkflowRequest) {
        val fields = request.fields.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }
        Log.d(
            TAG,
            "→ $command flow=$flow state=${request.document.state ?: "-"} event=${eventName ?: "-"} fields=$fields",
        )
    }

    override fun logResult(flow: String, response: WorkflowResponse) {
        Log.d(
            TAG,
            "← $flow result=${response.result} state=${response.state ?: "-"} screen=${response.screen?.title ?: "-"}",
        )
    }

    private companion object {
        const val TAG = "WorkflowSDUI"
    }
}
