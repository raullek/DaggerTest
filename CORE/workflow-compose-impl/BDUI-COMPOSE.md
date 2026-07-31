# Backend-Driven UI на Jetpack Compose (`core-workflow-compose`)

Второе, **параллельное** поколение SDUI-движка проекта: сервер описывает экран JSON'ом, клиент
рендерит его **на Jetpack Compose своим рендером**, собирает ввод и шлёт событие назад. В отличие от
XML-движка `:core-workflow` (RecyclerView + инфлейт ДС), здесь:

- рендер — `@Composable` (никаких View/ViewHolder/LayoutInflater);
- есть **межвиджетная реактивность** — protocol/capability-интерфейсы + `StrategyApplier`
  (двусторонние связи виджетов с отложенной сборкой), чего нет в XML-порте;
- движок/домен **self-contained** — модули НЕ зависят от `:core-workflow*` (сосуществующие
  поколения).

Ключевые узлы движка: Reflector-реестр, strategy/protocol-композиция, формат/валидация, engine-цикл.
Стек — корутины/Flow/Compose State (без RxJava); рефлектор собирается через Kotlin-DSL/Dagger, без
annotation-processor кодогена и без View-слоя.

---

## 1. Модули и пакеты

| Модуль | Что внутри |
|---|---|
| `:core-workflow-compose-api` | контракты: домен, engine, protocol, strategy, widget/reflector, format/validation/check, `WorkflowComposeFeatureApi`, launcher |
| `:core-workflow-compose-impl` | реализация + Compose-рендер + DI + фейковый сервер |
| `:core-designsystem` (`…compose`) | Compose-компоненты ДС (`DsTheme` + `Ds*`-композаблы) — соседствуют с XML |

Пакет: `az.less.core.workflow.compose`.

---

## 2. Слои (как в XML-движке, но рендер на Compose)

```
JSON ──► JsonParser ──► ResponseMapper ──► WfScreen/Widget/Field/Strategy (домен)
                                                  │
   BduiStateMachine (Loading/Screen/…) ◄── WorkflowRepository ◄── WorkflowApi (FakeBduiApi)
                                                  │
                         WidgetScope (контроллеры полей) + StrategyResolver
                                                  │
                         WorkflowHost (@Composable) ──► Reflector ──► Ds*-композаблы
```

| Слой | Где | Ответственность |
|---|---|---|
| Домен | `api/model` | `WfScreen→WfWidget→WfField`, `FieldType`, `FieldValidator`, **`StrategyDescriptor`**, `WfReferences`, transport |
| Транспорт | `impl/dto` + `mapper` | JSON→DTO→домен (компиляция валидаторов double-dispatch, парсинг `strategies`) |
| Источник | `impl/data` | `WorkflowApi` ◄ `FakeBduiApi` (stateful) + `WorkflowRepositoryImpl` |
| Движок | `impl/engine` | `BduiStateMachineImpl` (корутины/StateFlow, история/rollback) + `WidgetScopeImpl` |
| Протоколы | `api/protocol` | capability-интерфейсы (`Value/Description/Style/References/Visibility/Readonly/Error/Title`) |
| Стратегии | `api/strategy` + `impl/strategy` | `StrategyApplier`/`StrategyFactory` + `StrategyResolver` (отложенная сборка) + appliers |
| Контроллеры | `impl/controller` | state-holder'ы полей на `mutableStateOf`, реализуют протоколы |
| Reflector | `api/widget` + `impl/reflector` | `widget.type → композабл`, `field.type → рендерер/фабрика` (first-hit-wins + дефолты) |
| Рендер | `impl/ui` | `WorkflowHost` (3 региона) + widget/field-композаблы + `WorkflowComposeFragment` (ComposeView) |

---

## 3. Межвиджетная реактивность (главное отличие)

**Протоколы** — capability-интерфейсы (маркер `StrategyProtocol`). Контроллер реализует подмножество.
`StrategyApplier<Looking, LookUp>` объявляет, какие протоколы нужны у обеих сторон, и работает
СТРУКТУРНО — `StrategyResolver` проверяет `is`, не зная классов контроллеров.

**Отложенная сборка** (`StrategyResolver`): стратегия привязывается,
только когда оба контроллера (`lookingKey`/`lookUpKey`) существуют и протоколы совместимы; иначе ждёт
в pending. После привязки: применяется один раз (начальное состояние) и переподписывается на
`looking.observeChanges` (ввод пользователя).

**Echo-защита**: `FieldController.onUiInput` уведомляет слушателей
(драйвит стратегии), `setValueFromModel` — нет (стратегии не зацикливаются). Источник истины —
Compose-`State` контроллера; чтение в `@Composable` реактивно.

Готовые appliers (`impl/strategy/Appliers.kt`):

| `strategyType` | Looking | LookUp | Эффект |
|---|---|---|---|
| `updateDescription` | `ReferencesProtocol`+`ValueProtocol` | `MutableDescriptionProtocol`(+`Style`) | description/style выбранной опции → в целевое поле |
| `toggleVisibility` | `ValueProtocol` | `MutableVisibilityProtocol` | показать/скрыть цель по значению (`config.hideWhen`/`showWhen`) |

---

## 4. Расширяемость (через Dagger-реестры, без правок ядра)

| Хочу | Что сделать |
|---|---|
| Новый **виджет** (вкл. из фичи) | `WidgetRegistry` + `@Provides @IntoSet` → `Set<WidgetRegistry>` (ComplexReflector сшивает first-hit-wins) |
| Новый **тип поля** | добавить в `FieldRenderers.byType` + `DefaultControllerFactories.byType` |
| Новый **тип валидатора** | `ValidatorCompiler` + `@Binds @IntoMap @StringKey("…")` |
| Новая **стратегия** | `StrategyApplier` + ветка в `DefaultStrategyFactory` |
| Новый **форматтер** | пара в `DefaultFormatterRegistry` |
| **Реальный бэкенд** | заменить `@Binds FakeBduiApi` на Retrofit-реализацию `WorkflowApi` |

DI собирается как у `:core-workflow`: реестры (`Map<validator.type, ValidatorCompiler>`,
`Set<WidgetRegistry>`) агрегируются в AppComponent из `@IntoMap`/`@IntoSet`-вкладов и прокидываются в
`WorkflowComposeComponent` через `@BindsInstance`. Холдер (`WorkflowComposeHolderModule`,
`@IntoMap @ClassKey`) включён в `app/AppHolderModule`.

Скоупы: движок/репозиторий/api/валидатор результата — **без скоупа** (свежие на `newStateMachine()`;
`FakeBduiApi` так копит поля в рамках одного прогона); лаунчер/форматтеры/`StrategyFactory`/`Reflector`
— `@PerFeature`.

---

## 5. Демо-флоу `payment` (доказывает strategy-движок)

Запуск: Настройки → «Открыть перевод (BDUI на Compose)» →
`api<WorkflowComposeFeatureApi>().launcher().launch(fm, WorkflowFlows.PAYMENT)`.

- **Шаг 1 (recipient).** `SELECT recipientType` реактивно: (а) `updateDescription` меняет описание/стиль
  поля `target`; (б) `toggleVisibility` показывает поле `comment` (скрыто при «между своими счетами»).
- **Шаг 2 (amount).** `MONEY` (style `AMOUNT_FIELD`, крупное поле) + `SWITCH` + info-баннер в footer;
  MIN/MAX-валидация.
- **Шаг 3 (confirm).** `SUMMARY` (форматированные значения) + `CHECKBOX`-подтверждение; rollback «Назад».
- **END** → INFO-тост, закрытие флоу.

Логи экшенов — Logcat tag `BduiCompose`.

---

## 6. Сборка / запуск

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat :app:assembleDebug
```

Compose подключён плагином `org.jetbrains.kotlin.plugin.compose` (Kotlin 2.0.21), BOM в
`gradle/libs.versions.toml`.

---

## 7. Известные ограничения

1. **Rotation/process death** — состояние движка не сохраняется (нужен `SavedStateHandle`).
2. **Фейковый бэкенд** — `FakeBduiApi` знает флоу `payment` (swappable на Retrofit).
3. **Один экран на ответ** — сервер присылает ровно один экран, мульти-экранных ответов/степ-контроллера нет.
4. **Ввод без on-the-fly форматирования** — UI-форматтер применяется для readonly/summary (`displayValue`)
   и на сбор (`collect`); редактируемые поля показывают «натуральное» значение.
5. **kapt** (fallback на Kotlin 1.9) — мигрировать на KSP.
