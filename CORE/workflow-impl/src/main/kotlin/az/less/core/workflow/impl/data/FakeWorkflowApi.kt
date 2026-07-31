package az.less.core.workflow.impl.data

import az.less.core.workflow.api.WorkflowFlows
import az.less.core.workflow.api.model.Command
import az.less.core.workflow.api.model.WorkflowRequest
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Фейковый in-memory «сервер» server-driven UI: на команду отдаёт захардкоженный JSON следующего
 * экрана. Демонстрирует весь конвейер без реального бэкенда (свапается на Retrofit-реализацию
 * [WorkflowApi] без правок движка/рендера).
 *
 * Инстанс живёт один прогон флоу (репозиторий/движок без скоупа), поэтому [transferData] корректно
 * накапливает поля по шагам перевода — как stateful-сессия на сервере.
 */
class FakeWorkflowApi @Inject constructor() : WorkflowApi {

    private val transferData = mutableMapOf<String, String>()

    override suspend fun doEvent(
        command: Command,
        flow: String,
        pid: String?,
        eventName: String?,
        request: WorkflowRequest,
    ): String {
        delay(NETWORK_DELAY_MS)
        val state = request.document.state
        return when (flow) {
            WorkflowFlows.LOAN -> loanStep(command, state, eventName)
            WorkflowFlows.PROFILE_EDIT -> profileStep(command, eventName)
            WorkflowFlows.FEEDBACK -> feedbackStep(command, eventName)
            WorkflowFlows.TRANSFER -> transferStep(command, state, eventName, request)
            else -> error("Неизвестный флоу: $flow")
        }
    }

    private fun loanStep(command: Command, state: String?, event: String?): String = when {
        command == Command.START -> LOAN_APPLICANT
        state == "applicant" && event == "NEXT" -> LOAN_CONTACTS
        state == "contacts" && event == "SUBMIT" -> LOAN_END
        else -> error("loan: неизвестный переход state=$state, event=$event")
    }

    private fun profileStep(command: Command, event: String?): String = when {
        command == Command.START -> PROFILE_SCREEN
        event == "SAVE" -> PROFILE_END
        else -> error("profile_edit: неизвестный переход event=$event")
    }

    private fun feedbackStep(command: Command, event: String?): String = when {
        command == Command.START -> FEEDBACK_SCREEN
        event == "SUBMIT" -> FEEDBACK_END
        else -> error("feedback: неизвестный переход event=$event")
    }

    // --- transfer: 4 шага + экран успеха, с накоплением полей ---

    private fun transferStep(command: Command, state: String?, event: String?, request: WorkflowRequest): String {
        if (command == Command.START) {
            transferData.clear()
            return TRANSFER_RECIPIENT
        }
        transferData.putAll(request.fields)
        return when {
            state == "recipient" && event == "NEXT" -> TRANSFER_SOURCE
            state == "source" && event == "NEXT" -> TRANSFER_AMOUNT
            state == "amount" && event == "NEXT" -> confirmScreen()
            state == "confirm" && event == "CONFIRM" -> successScreen()
            state == "success" && event == "DONE" -> TRANSFER_END
            else -> error("transfer: неизвестный переход state=$state, event=$event")
        }
    }

    private fun accountTitle(): String = ACCOUNTS[transferData["sourceAccount"]] ?: "—"

    private fun confirmScreen(): String {
        val phone = transferData["phone"].orEmpty()
        val amount = transferData["amount"].orEmpty()
        return """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-TRANSFER", "flow": "transfer", "state": "confirm",
                "screen": {
                  "title": "Подтверждение", "description": "Проверьте детали перевода",
                  "properties": { "step": "4", "steps": "4" },
                  "widgets": [
                    { "type": "SUMMARY", "title": "Перевод", "fields": [
                      { "id": "sumRecipient", "type": "PHONE", "title": "Получатель", "value": "$phone", "readonly": true },
                      { "id": "sumAccount", "type": "TEXT", "title": "Счёт списания", "value": "${accountTitle()}", "readonly": true },
                      { "id": "sumAmount", "type": "MONEY", "title": "Сумма", "value": "$amount", "readonly": true } ] },
                    { "type": "FIELDSET", "fields": [
                      { "id": "agree", "type": "CHECKBOX", "title": "Подтверждаю перевод",
                        "validators": [ {"type":"REQUIRED","message":"Нужно подтверждение"} ] } ] }
                  ]
                },
                "events": [
                  {"name":"BACK","title":"Назад","type":"ROLLBACK"},
                  {"name":"CONFIRM","title":"Перевести","type":"SUBMIT"} ]
              }
            }
        """.trimIndent()
    }

    private fun successScreen(): String {
        val phone = transferData["phone"].orEmpty()
        val amount = transferData["amount"].orEmpty()
        return """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-TRANSFER", "flow": "transfer", "state": "success",
                "screen": {
                  "title": "Перевод выполнен", "description": null,
                  "widgets": [
                    { "type": "BANNER", "title": "Деньги отправлены получателю", "properties": { "tone": "success" } },
                    { "type": "TRANSFER_RECEIPT", "properties": {
                        "amount": "$amount", "recipient": "$phone", "account": "${accountTitle()}" } }
                  ]
                },
                "events": [ {"name":"DONE","title":"Готово","type":"SUBMIT"} ]
              }
            }
        """.trimIndent()
    }

    private companion object {
        const val NETWORK_DELAY_MS = 400L

        val ACCOUNTS = mapOf(
            "acc1" to "Зарплатная •• 1234 (45 000 ₽)",
            "acc2" to "Накопительная •• 7788 (120 000 ₽)",
        )

        val TRANSFER_RECIPIENT = """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-TRANSFER", "flow": "transfer", "state": "recipient",
                "screen": {
                  "title": "Кому перевести", "description": "Шаг 1 — получатель",
                  "properties": { "step": "1", "steps": "4" },
                  "widgets": [ { "type": "FIELDSET", "title": "Получатель", "fields": [
                    { "id": "phone", "type": "PHONE", "title": "Телефон получателя",
                      "validators": [ {"type":"REQUIRED","message":"Укажите телефон"} ] },
                    { "id": "name", "type": "TEXT", "title": "Имя (необязательно)" } ] } ]
                },
                "events": [ {"name":"NEXT","title":"Далее","type":"SUBMIT"} ]
              }
            }
        """.trimIndent()

        val TRANSFER_SOURCE = """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-TRANSFER", "flow": "transfer", "state": "source",
                "screen": {
                  "title": "Откуда списать", "description": "Шаг 2 — счёт списания",
                  "properties": { "step": "2", "steps": "4" },
                  "widgets": [ { "type": "FIELDSET", "title": "Счёт списания", "fields": [
                    { "id": "sourceAccount", "type": "RADIO", "title": "Выберите счёт", "referenceId": "accounts",
                      "validators": [ {"type":"REQUIRED","message":"Выберите счёт"} ] } ] } ]
                },
                "events": [
                  {"name":"BACK","title":"Назад","type":"ROLLBACK"},
                  {"name":"NEXT","title":"Далее","type":"SUBMIT"} ],
                "references": { "accounts": [
                  {"id":"acc1","text":"Зарплатная •• 1234 (45 000 ₽)"},
                  {"id":"acc2","text":"Накопительная •• 7788 (120 000 ₽)"} ] }
              }
            }
        """.trimIndent()

        val TRANSFER_AMOUNT = """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-TRANSFER", "flow": "transfer", "state": "amount",
                "screen": {
                  "title": "Сумма перевода", "description": "Шаг 3 — сколько и когда",
                  "properties": { "step": "3", "steps": "4" },
                  "widgets": [
                    { "type": "FIELDSET", "fields": [
                      { "id": "amount", "type": "MONEY", "style": "AMOUNT_FIELD", "title": "Сумма", "validators": [
                          {"type":"REQUIRED","message":"Укажите сумму"},
                          {"type":"MIN_VALUE","value":"1","message":"Сумма должна быть больше нуля"} ] },
                      { "id": "comment", "type": "TEXT", "title": "Комментарий (необязательно)" },
                      { "id": "now", "type": "SWITCH", "title": "Перевести сейчас", "value": "true" } ] }
                  ],
                  "footer": [
                    { "type": "BANNER", "title": "Перевод без комиссии", "properties": { "tone": "info" } }
                  ]
                },
                "events": [
                  {"name":"BACK","title":"Назад","type":"ROLLBACK"},
                  {"name":"NEXT","title":"Далее","type":"SUBMIT"} ]
              }
            }
        """.trimIndent()

        val TRANSFER_END = """
            { "body": { "result": "END", "pid":"PID-TRANSFER", "flow":"transfer", "state":"done" } }
        """.trimIndent()

        val LOAN_APPLICANT = """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-LOAN", "flow": "loan", "state": "applicant",
                "screen": {
                  "title": "Заявка на кредит", "description": "Шаг 1 из 2 — анкета",
                  "widgets": [ { "type": "FIELDSET", "title": "О заёмщике", "fields": [
                    { "id": "fullName", "type": "TEXT", "title": "ФИО", "validators": [
                        {"type":"REQUIRED","message":"Укажите ФИО"},
                        {"type":"MIN_LENGTH","value":"3","message":"Минимум 3 символа"} ] },
                    { "id": "age", "type": "INTEGER", "title": "Возраст", "validators": [
                        {"type":"REQUIRED","message":"Укажите возраст"},
                        {"type":"MIN_VALUE","value":"18","message":"Только с 18 лет"},
                        {"type":"MAX_VALUE","value":"120","message":"Проверьте возраст"} ] },
                    { "id": "gender", "type": "SELECT", "title": "Пол", "referenceId": "genders",
                      "validators": [ {"type":"REQUIRED","message":"Выберите пол"} ] } ] } ]
                },
                "events": [ {"name":"NEXT","title":"Далее","type":"SUBMIT"} ],
                "references": { "genders": [ {"id":"M","text":"Мужской"}, {"id":"F","text":"Женский"} ] }
              }
            }
        """.trimIndent()

        val LOAN_CONTACTS = """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-LOAN", "flow": "loan", "state": "contacts",
                "screen": {
                  "title": "Контакты и доход", "description": "Шаг 2 из 2",
                  "widgets": [
                    { "type": "FIELDSET", "title": "Контакты", "fields": [
                      { "id": "phone", "type": "PHONE", "title": "Телефон",
                        "validators": [ {"type":"REQUIRED","message":"Укажите телефон"} ] },
                      { "id": "birthDate", "type": "DATE", "title": "Дата рождения",
                        "validators": [ {"type":"REQUIRED","message":"Укажите дату"} ] } ] },
                    { "type": "FIELDSET", "title": "Доход", "fields": [
                      { "id": "income", "type": "MONEY", "title": "Ежемесячный доход", "validators": [
                          {"type":"REQUIRED","message":"Укажите доход"},
                          {"type":"MIN_VALUE","value":"0","message":"Доход не может быть отрицательным"} ] },
                      { "id": "agree", "type": "CHECKBOX", "title": "Согласен на обработку данных",
                        "validators": [ {"type":"REQUIRED","message":"Нужно согласие"} ] } ] }
                  ]
                },
                "events": [
                  {"name":"BACK","title":"Назад","type":"ROLLBACK"},
                  {"name":"SUBMIT","title":"Отправить","type":"SUBMIT"} ]
              }
            }
        """.trimIndent()

        val LOAN_END = """
            { "body": { "result": "END", "pid":"PID-LOAN", "flow":"loan", "state":"done",
              "messages": [ {"type":"INFO","text":"Заявка принята! Решение придёт в push."} ] } }
        """.trimIndent()

        val PROFILE_SCREEN = """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-PROFILE", "flow": "profile_edit", "state": "edit",
                "screen": {
                  "title": "Редактирование профиля", "description": "Обновите контактные данные",
                  "widgets": [ { "type": "FIELDSET", "title": "Контакты", "fields": [
                    { "id": "fullName", "type": "TEXT", "title": "Имя", "value": "Мир Рашад",
                      "validators": [ {"type":"REQUIRED","message":"Укажите имя"},
                                      {"type":"MIN_LENGTH","value":"2","message":"Слишком коротко"} ] },
                    { "id": "email", "type": "TEXT", "title": "E-mail", "value": "mirrashadhasanov@gmail.com",
                      "validators": [ {"type":"REQUIRED","message":"Укажите e-mail"},
                                      {"type":"REGEXP","value":"^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$","message":"Неверный e-mail"} ] },
                    { "id": "phone", "type": "PHONE", "title": "Телефон", "value": "79990000000",
                      "validators": [ {"type":"REQUIRED","message":"Укажите телефон"} ] } ] } ]
                },
                "events": [ {"name":"SAVE","title":"Сохранить","type":"SUBMIT"} ]
              }
            }
        """.trimIndent()

        val PROFILE_END = """
            { "body": { "result": "END", "pid":"PID-PROFILE", "flow":"profile_edit", "state":"done",
              "messages": [ {"type":"INFO","text":"Профиль сохранён."} ] } }
        """.trimIndent()

        val FEEDBACK_SCREEN = """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-FB", "flow": "feedback", "state": "rate",
                "screen": {
                  "title": "Оставить отзыв", "description": "Поделитесь впечатлением о приложении",
                  "widgets": [ { "type": "FIELDSET", "title": "Ваша оценка", "fields": [
                    { "id": "rating", "type": "SELECT", "title": "Оценка", "referenceId": "ratings",
                      "validators": [ {"type":"REQUIRED","message":"Поставьте оценку"} ] },
                    { "id": "recommend", "type": "CHECKBOX", "title": "Порекомендую друзьям" },
                    { "id": "comment", "type": "TEXT", "title": "Комментарий", "validators": [
                        {"type":"REQUIRED","message":"Напишите пару слов"},
                        {"type":"MIN_LENGTH","value":"5","message":"Слишком коротко"} ] } ] } ]
                },
                "events": [ {"name":"SUBMIT","title":"Отправить отзыв","type":"SUBMIT"} ],
                "references": { "ratings": [
                  {"id":"5","text":"★★★★★ Отлично"}, {"id":"4","text":"★★★★ Хорошо"},
                  {"id":"3","text":"★★★ Нормально"}, {"id":"2","text":"★★ Плохо"},
                  {"id":"1","text":"★ Ужасно"} ] }
              }
            }
        """.trimIndent()

        val FEEDBACK_END = """
            { "body": { "result": "END", "pid":"PID-FB", "flow":"feedback", "state":"done",
              "messages": [ {"type":"INFO","text":"Спасибо за отзыв!"} ] } }
        """.trimIndent()
    }
}
