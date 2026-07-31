package az.less.feature.transfer.api

import androidx.fragment.app.FragmentManager

/** Точка входа в фичу перевода (как у любой фичи), но внутри — запуск SDUI-флоу. */
interface TransferLauncher {
    fun launch(fragmentManager: FragmentManager)
}
