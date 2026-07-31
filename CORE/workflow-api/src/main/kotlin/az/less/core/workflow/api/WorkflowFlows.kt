package az.less.core.workflow.api

/**
 * Имена доступных server-driven флоу — типобезопасные константы вместо «магических» строк.
 * Фичи передают их в [navigation.WorkflowLauncher.launch]; фейковый сервер диспетчеризует по ним.
 */
object WorkflowFlows {
    /** Заявка на кредит (2 экрана) — запускается из Настроек. */
    const val LOAN = "loan"

    /** Редактирование профиля (1 экран) — запускается из Профиля. */
    const val PROFILE_EDIT = "profile_edit"

    /** Отзыв об приложении (1 экран) — запускается из Каталога. */
    const val FEEDBACK = "feedback"

    /** Перевод денег (4 шага + успех) — целиком SDUI-фича transfer. */
    const val TRANSFER = "transfer"
}
