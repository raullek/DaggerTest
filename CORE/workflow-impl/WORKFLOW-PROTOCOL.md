# Протокол бэкенда SDUI (workflow)

Контракт, который должен реализовать **сервер**, чтобы работать с текущим SDUI-клиентом. Клиент
«тонкий»: он рисует то, что прислал сервер, валидирует на клиенте по присланным правилам (только UX),
собирает ввод и отправляет событие. **Вся логика флоу — на сервере.** Спека описывает текущую
реализацию (см. `WORKFLOW.md` для устройства клиента).

---

## 1. Транспорт

Одна логическая операция — «обработать экшн»:

```
doEvent(command, flow, pid, eventName, body) -> Response(JSON)
```

В коде это интерфейс `WorkflowApi.doEvent(command, flow, pid, eventName, request): String`. Текущая
реализация — фейк (`FakeWorkflowApi`); боевой бэкенд = Retrofit-реализация `WorkflowApi`. Предлагаемая
HTTP-форма (на усмотрение реализации — клиенту важен только возвращаемый JSON):

```
POST {base}/workflow/{flow}/event?command={START|EVENT}&event={eventName}&pid={pid}
Content-Type: application/json
<тело запроса>           →  200 OK  <тело ответа: { "body": { … } }>
```

---

## 2. Команды (client → server)

| Команда | Когда | Что шлёт клиент | Что делает сервер |
|---|---|---|---|
| `START` | старт флоу | `pid=null`, `eventName=null`, `document={flow}`, `fields={}` | создать сессию/документ, вернуть первый экран (`result=SCREEN`, новый `pid`) |
| `EVENT` | нажата кнопка-событие | `eventName`, `pid` из последнего ответа, `document={flow,state,documentId}`, `fields` текущего экрана | применить поля, провалидировать, перейти в следующий стейт → вернуть экран или `END` или ошибки |
| `ROLLBACK` | «Назад» | **НЕ отправляется** | — клиент откатывается локально (стек экранов), сервер о «назад» не уведомляется |
| `EXIT` | — | зарезервирована, клиент не шлёт | — |

> **Нюанс «назад»:** событие с `type:"ROLLBACK"` или `name:"BACK"` клиент обрабатывает локально и
> на сервер ничего не шлёт. Поэтому сервер **не должен** рассчитывать на серверный rollback. Если он
> нужен — это доработка клиента.

---

## 3. Формат запроса (client → server)

```json
{
  "document": {
    "flow": "transfer",
    "state": "amount",
    "documentId": "PID-TRANSFER",
    "additional": {}
  },
  "fields": { "amount": "150000", "comment": "за обед", "now": "true" }
}
```

- `document.flow/state/documentId` — **эхо** из последнего ответа (синхронизация). Авторитет — у
  сервера: он опирается на своё состояние по `pid`/`documentId`, а присланные `state`/`fields`
  трактует как ввод текущего шага.
- `fields` — значения **только видимого экрана** (`fieldId → value`). Многошаговому флоу сервер сам
  аккумулирует поля по `documentId`/`pid` между шагами (как делает фейк).
- На `START` тело — `{document:{flow}}`, `fields` пустые.

---

## 4. Формат ответа (server → client)

```json
{
  "body": {
    "result": "SCREEN",
    "pid": "PID-TRANSFER",
    "flow": "transfer",
    "state": "amount",
    "screen": {
      "title": "Сумма перевода",
      "description": "Шаг 3 — сколько и когда",
      "properties": { "step": "3", "steps": "4" },
      "header": [ /* виджеты закреплённой шапки (необязательно) */ ],
      "widgets": [ /* виджеты тела (скроллится) */ ],
      "footer": [ /* виджеты закреплённого подвала (необязательно) */ ]
    },
    "events": [ { "name": "NEXT", "title": "Далее", "type": "SUBMIT", "hidden": false } ],
    "references": { "<refId>": [ { "id": "…", "text": "…" } ] },
    "fieldMessages": { "<fieldId>": { "type": "ERROR", "code": "…", "text": "…" } },
    "messages": [ { "type": "INFO", "code": "…", "text": "…" } ],
    "exitUri": null
  }
}
```

| Поле | Назначение |
|---|---|
| `result` | `SCREEN` — показать экран; `END` — завершить флоу |
| `pid` | id процесса/документа; клиент эхом вернёт его в `document.documentId` и параметром `pid` |
| `state` | имя текущего стейта; клиент эхом вернёт в `document.state` |
| `screen.header/widgets/footer` | три региона (см. §5); все — списки виджетов |
| `events` | кнопки внизу (footer); пустой массив — кнопок нет |
| `references` | справочники для `SELECT`/`RADIO` (общие на весь экран) |
| `fieldMessages` | серверные ошибки под конкретные поля (`fieldId → message`) |
| `messages` | экранные сообщения (INFO/ERROR/FATAL) |
| `exitUri` | куда уйти после `END` (диплинк); сейчас клиент просто закрывает флоу |

---

## 5. Регионы экрана (header / body / footer)

Клиент рендерит экран в **три региона** (header/main/footer):

- **header** — закреплённая шапка: клиент сам рисует степпер (`properties.step/steps`) и заголовок
  (`title`/`description`), затем `screen.header[]`-виджеты. Не скроллится.
- **body** — `screen.widgets[]` в RecyclerView. Скроллится.
- **footer** — `screen.footer[]`-виджеты, затем кнопки `events`. Закреплён снизу, не скроллится.

Сервер раскладывает виджеты по регионам, помещая их в нужный массив. `HEADER`/`STEPPER`/`EVENT` —
**клиентские** структурные элементы, сервер их НЕ шлёт (степпер задаётся через `properties`).

---

## 6. Машина состояний на сервере

Сервер держит per-`pid`/`documentId` состояние (текущий стейт + накопленные данные):

```
START                          → создать документ, вернуть экран state=S0 (result=SCREEN, pid=новый)
EVENT(state=Si, event=E, …)    → применить fields, серверно провалидировать,
                                   ├─ ок   → перейти Si→Sj, вернуть экран Sj (result=SCREEN)
                                   │         либо завершить (result=END [, exitUri])
                                   └─ нет  → вернуть тот же/новый экран + fieldMessages/messages(ERROR)
```

- **Экран успеха** — это обычный `SCREEN` (напр. `state=success`) с терминальным событием (`DONE`),
  которое на следующем `EVENT` сервер мапит в `result=END`.
- **Идемпотентность:** рекомендуется делать переход по `(pid, state, event)` идемпотентным (повтор из
  того же стейта даёт тот же результат) — клиент может ретраить.
- Клиент показывает **один экран на ответ** (нет нескольких экранов/StepController в одном ответе).

---

## 7. Валидация (два уровня)

1. **Клиентская (UX-фильтр).** Сервер кладёт `validators[]` в поля; клиент компилирует и проверяет
   **перед отправкой**, блокирует submit и показывает `message` под полем. Это не безопасность.
2. **Серверная (авторитетная).** Сервер ВСЕГДА перепроверяет. При ошибке возвращает экран +
   `fieldMessages` (под поля) и/или `messages` с `type:"ERROR"` — клиент остаётся на экране и
   показывает сообщения (тосты + ошибки полей). `type:"FATAL"` → клиент показывает и **закрывает флоу**.

---

## 8. Словарь (что понимает клиент)

### Типы полей и формат значения (server value conventions)

| `field.type` | UI | Значение в `value`/`fields` |
|---|---|---|
| `TEXT` | строка | как есть |
| `INTEGER` | число | целое строкой |
| `DECIMAL` | число | десятичное строкой |
| `MONEY` | поле денег | **целое в минимальных единицах** (копейки): `"150000"` = 1 500,00 ₽ |
| `DATE` | дата | **ISO** `yyyy-MM-dd` |
| `PHONE` | телефон | **11 цифр** без форматирования: `"79001234567"` |
| `SELECT` | выпадающий список | `id` элемента из `references[referenceId]` |
| `RADIO` | радиогруппа | `id` элемента из `references[referenceId]` |
| `CHECKBOX` | чекбокс | `"true"` (вкл) или `""` (выкл) |
| `SWITCH` | переключатель | `"true"` / `""` |

Неизвестный `field.type` → клиент рисует поле read-only (не падает).

### Типы виджетов (`widget.type`)

| Тип | Рендер |
|---|---|
| `FIELDSET` | карточка с полями (`widget.fields[]`) |
| `SUMMARY` | строки «подпись ↔ значение» (read-only поля, форматируются по типу) |
| `BANNER` | баннер; `widget.properties.tone` = `info` (по умолч.) / `success` |
| *кастомный* (напр. `TRANSFER_RECEIPT`) | вьюхолдер, зарегистрированный фичей через `@IntoMap @WidgetTypeKey` |

> **Расширяемость/деградация:** клиент рендерит только те `widget.type`, что зарегистрированы в реестре
> (ядро + фичи). **Неизвестный `widget.type` — пропускается** (экран не падает). Поэтому новый виджет
> сначала добавляется на клиент (зарегистрированная фабрика), затем сервер может его слать.

### Типы валидаторов (`validator.type`)

`REQUIRED`, `MIN_LENGTH` (value=число), `MAX_LENGTH` (value=число), `REGEXP` (value=паттерн),
`MIN_VALUE` / `MAX_VALUE` (value=число; применяются только к числовым полям). Неизвестный тип —
игнорируется.

### Типы сообщений (`message.type`)

`INFO` (показать, напр. на `END`) · `ERROR` (остаться на экране) · `FATAL` (показать и закрыть флоу).

### Properties

- `screen.properties.step` / `steps` — степпер в шапке (например `"3"` / `"4"`).
- `widget.properties.tone` — `info`/`success` для `BANNER`.
- `field.style` — имя компонента дизайн-системы для оверрайда (напр. `"AMOUNT_FIELD"` для крупного
  ввода суммы у `MONEY`-поля).
- произвольные ключи `widget.properties` — данные кастомного виджета (напр. `TRANSFER_RECEIPT`:
  `amount`/`recipient`/`account`).

---

## 9. События и навигация

- `event` = `{ name, title, type, hidden }`.
- `type:"SUBMIT"` (или любой не-rollback) → клиент валидирует поля и шлёт `EVENT(name)` c `fields`.
- `type:"ROLLBACK"` **или** `name:"BACK"` → клиент откатывается **локально** (на сервер не идёт).
- `hidden:true` → кнопка не рисуется (форм-/авто-триггер).

---

## 10. Пример: флоу `transfer` (4 шага + успех)

| # | Запрос клиента | Ответ сервера |
|---|---|---|
| 1 | `START` `{document:{flow:transfer}}` | `SCREEN state=recipient` (header: степпер 1/4; body: FIELDSET phone/name; footer: events[NEXT]) |
| 2 | `EVENT NEXT` `state=recipient fields={phone,name}` | `SCREEN state=source` (body: RADIO `references.accounts`; events[BACK,NEXT]) |
| 3 | `EVENT NEXT` `state=source fields={sourceAccount}` | `SCREEN state=amount` (body: AMOUNT+SWITCH; **footer: BANNER «без комиссии»** + events) |
| 4 | `EVENT NEXT` `state=amount fields={amount,comment,now}` | `SCREEN state=confirm` (body: SUMMARY накопленного + CHECKBOX; events[BACK,CONFIRM]) |
| 5 | `EVENT CONFIRM` `state=confirm fields={agree}` | `SCREEN state=success` (body: BANNER success + кастомный `TRANSFER_RECEIPT`; events[DONE]) |
| 6 | `EVENT DONE` `state=success` | `END` |

Сервер на шагах 2–5 **аккумулирует** поля по `pid`, а на `confirm`/`success` строит SUMMARY/квитанцию
из накопленного (так делает `FakeWorkflowApi`).

---

## 11. Нюансы и требования к бэкенду

1. **Безопасность:** всю валидацию/лимиты/права дублировать на сервере; клиентские `validators` —
   только UX, доверять им нельзя.
2. **Состояние по pid:** сервер хранит документ/сессию по `pid`; клиент лишь эхом возвращает
   `state`/`documentId`. Не доверять присланному `state` как источнику истины.
3. **Поля только текущего экрана:** клиент шлёт `fields` видимого экрана; межшаговую агрегацию делает
   сервер.
4. **`masked`-поля:** если сервер прислал `field.masked:true` с замаскированным значением — клиент
   сейчас отправляет его обратно как есть (ограничение). Для боевого: сервер должен уметь отличать
   «значение не менялось» (не перезаписывать оригинал).
5. **Неизвестные типы:** неизвестный `widget.type` пропускается, неизвестный `field.type` → read-only,
   неизвестный `validator.type` игнорируется — экран не падает, но виджет не покажется, пока не
   добавлен на клиент.
6. **Один экран на ответ**, `references` общие на ответ.
7. **`exitUri`** на `END` — целевой диплинк; сейчас клиент его не использует (просто закрывает флоу).
8. **Логирование:** клиент логирует каждый исходящий экшн (команда/флоу/стейт/событие/`fields`) и
   результат — Logcat tag `WorkflowSDUI` (см. `WorkflowLogger`).
