package az.less.feature.transfer.impl.navigation

import androidx.fragment.app.FragmentManager
import az.less.core.di.api
import az.less.core.workflow.api.WorkflowFeatureApi
import az.less.core.workflow.api.WorkflowFlows
import az.less.feature.transfer.api.TransferLauncher
import javax.inject.Inject

/**
 * Вся фича — тонкая оболочка над ядром SDUI: лаунчер просто открывает флоу `transfer` в
 * workflow-хосте. Никакого собственного UI у фичи нет — экраны приходят с сервера.
 */
internal class TransferLauncherImpl @Inject constructor() : TransferLauncher {

    override fun launch(fragmentManager: FragmentManager) {
        api<WorkflowFeatureApi>().launcher().launch(fragmentManager, WorkflowFlows.TRANSFER)
    }
}
