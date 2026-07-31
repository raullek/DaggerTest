package az.less.core.workflow.compose.impl.data

import az.less.core.workflow.compose.api.WorkflowFlows
import az.less.core.workflow.compose.api.model.Command
import az.less.core.workflow.compose.api.model.WorkflowRequest
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Фейковый in-memory BDUI-«сервер». Отдаёт захардкоженный JSON следующего экрана. Демонстрирует весь
 * конвейер (включая **межвиджетные стратегии**) без реального бэкенда; свапается на Retrofit без
 * правок движка/рендера. Stateful: накапливает поля по шагам (живёт один прогон флоу).
 *
 * Демо-флоу `payment` доказывает strategy-движок: на 1-м шаге `recipientType` (SELECT) реактивно
 * (а) меняет описание/стиль поля `target` (стратегия `updateDescription`) и (б) показывает/прячет
 * поле `comment` (стратегия `toggleVisibility`, скрыто при выборе «между счетами»).
 */
class FakeBduiApi @Inject constructor() : WorkflowApi {

    private val data = mutableMapOf<String, String>()

    override suspend fun doEvent(
        command: Command,
        flow: String,
        pid: String?,
        eventName: String?,
        request: WorkflowRequest,
    ): String {
        delay(NETWORK_DELAY_MS)
        if (command == Command.START) {
            data.clear()
            return PAYMENT_RECIPIENT
        }
        data.putAll(request.fields)
        val state = request.document.state
        return when (flow) {
            WorkflowFlows.PAYMENT -> paymentStep(state, eventName)
            else -> error("Неизвестный флоу: $flow")
        }
    }

    private fun paymentStep(state: String?, event: String?): String = when {
        state == "recipient" && event == "NEXT" -> PAYMENT_AMOUNT
        state == "amount" && event == "NEXT" -> confirmScreen()
        state == "confirm" && event == "CONFIRM" -> PAYMENT_END
        else -> error("payment: неизвестный переход state=$state, event=$event")
    }

    private fun recipientTitle(): String {
        val type = data["recipientType"]
        val target = data["target"].orEmpty()
        return when (type) {
            "phone" -> "Телефон $target"
            "card" -> "Карта $target"
            "self" -> "Свой счёт"
            else -> target
        }
    }

    private fun confirmScreen(): String {
        val amount = data["amount"].orEmpty()
        return """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-PAY", "flow": "payment", "state": "confirm",
                "screen": {
                  "title": "Подтверждение", "description": "Проверьте детали перевода",
                  "properties": { "step": "3", "steps": "3" },
                  "widgets": [
                    { "type": "SUMMARY", "title": "Перевод", "fields": [
                      { "id": "sumTo", "type": "TEXT", "title": "Получатель", "value": "${recipientTitle()}", "readonly": true },
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

    private companion object {
        const val NETWORK_DELAY_MS = 350L

        // Шаг 1: SELECT recipientType со стратегиями на target (описание/стиль) и comment (видимость).
        val PAYMENT_RECIPIENT = """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-PAY", "flow": "payment", "state": "recipient",
                "screen": {
                  "title": "Перевод", "description": "Шаг 1 из 3 — получатель",
                  "properties": { "step": "1", "steps": "3" },
                  "widgets": [
                    { "type": "FIELDSET", "title": "Куда перевести", "fields": [
                      { "id": "recipientType", "type": "SELECT", "title": "Тип перевода", "referenceId": "recipientTypes",
                        "validators": [ {"type":"REQUIRED","message":"Выберите тип"} ] },
                      { "id": "target", "type": "TEXT", "title": "Получатель",
                        "description": "Сначала выберите тип перевода",
                        "validators": [ {"type":"REQUIRED","message":"Укажите получателя"} ] },
                      { "id": "comment", "type": "TEXT", "title": "Комментарий (необязательно)", "visible": false } ] }
                  ],
                  "strategies": [
                    { "type": "updateDescription", "lookingKey": "recipientType", "lookUpKey": "target" },
                    { "type": "toggleVisibility", "lookingKey": "recipientType", "lookUpKey": "comment",
                      "config": { "hideWhen": "self" } }
                  ]
                },
                "events": [ {"name":"NEXT","title":"Далее","type":"SUBMIT"} ],
                "references": { "recipientTypes": [
                  {"id":"phone","text":"По телефону","description":"Введите номер телефона получателя","style":"PHONE_FIELD"},
                  {"id":"card","text":"По карте","description":"Введите 16 цифр номера карты"},
                  {"id":"self","text":"Между своими счетами","description":"Перевод между вашими счетами"} ] }
              }
            }
        """.trimIndent()

        // Шаг 2: сумма (AMOUNT) + переключатель + баннер в footer.
        val PAYMENT_AMOUNT = """
            {
              "body": {
                "result": "SCREEN", "pid": "PID-PAY", "flow": "payment", "state": "amount",
                "screen": {
                  "title": "Сумма перевода", "description": "Шаг 2 из 3",
                  "properties": { "step": "2", "steps": "3" },
                  "widgets": [
                    { "type": "FIELDSET", "fields": [
                      { "id": "amount", "type": "MONEY", "style": "AMOUNT_FIELD", "title": "Сумма", "validators": [
                          {"type":"REQUIRED","message":"Укажите сумму"},
                          {"type":"MIN_VALUE","value":"1","message":"Сумма должна быть больше нуля"},
                          {"type":"MAX_VALUE","value":"1000000","message":"Слишком большая сумма"} ] },
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

        val PAYMENT_END = """
            { "body": { "result": "END", "pid":"PID-PAY", "flow":"payment", "state":"done",
              "messages": [ {"type":"INFO","text":"Перевод выполнен!"} ] } }
        """.trimIndent()
    }
}
