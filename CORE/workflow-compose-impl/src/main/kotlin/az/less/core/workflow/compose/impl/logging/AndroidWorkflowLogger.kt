package az.less.core.workflow.compose.impl.logging

import android.util.Log
import az.less.core.workflow.compose.api.engine.WorkflowLogger
import javax.inject.Inject

/** Логгер экшенов BDUI в Logcat (tag `BduiCompose`). Подмени биндинг для аналитики. */
class AndroidWorkflowLogger @Inject constructor() : WorkflowLogger {
    override fun logEvent(message: String) {
        Log.d(TAG, message)
    }

    private companion object {
        const val TAG = "BduiCompose"
    }
}
