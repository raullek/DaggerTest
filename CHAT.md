# Чат с ассистентом — экран-агрегатор на Dagger-мультибиндингах

Экран собирает вклады всех фич через `@IntoMap`/`@IntoSet`, ничего о самих фичах не зная,
поверх DI-каркаса проекта (`FeatureHolder`-реестр).
Два рендера одного экрана: XML (RecyclerView) и Jetpack Compose — поверх ОБЩЕГО домена.

## Устройство агрегации

| Элемент | Роль |
|---|---|
| `Map<String, ChatWidgetViewHolderFactory>` / `Map<String, ChatWidgetComposer>` (@IntoMap @StringKey) | реестры рендеров виджетов, по карте на каждый рендер |
| `Set<ChatQuickAction>` (@IntoSet, title/isAvailable/onClick) | «визитки» фич в ряду чипов, навигация «с собой» |
| server-driven `widgetId` от `FakeChatServer` → key-lookup в карте | диспатч виджета по строковому ключу |
| фолбэк «виджет не поддерживается» на неизвестный id | неизвестный ключ — не ошибка |
| `app/di/chat/CompoundChatWidgetsModule` (только includes) | единая точка подключения вкладов всех фич |
| `app/di/chat/ChatComponent` + `ChatFeatureHolder` | компонент фичи-агрегатора и его ленивый холдер |
| `ChatWidgetRegistry` | контейнер над сырыми агрегатами |
| `@Multibinds`-швы в `ChatModule` | карты/набор существуют, даже если вкладов нет |
| `ChatInternalApi : ChatFeatureApi` + `ChatModule` (public, в impl) | внутренний контракт графа для app-модуля |

Компонент фичи-агрегатора объявлен в **app-модуле** (единственном месте,
видящем все impl) и включает Compound-модуль прямо в `modules = [...]` — мультибиндинги
материализуются в графе самой фичи, лениво, при первом `getFeature()`. Корневой `AppComponent`
получает от чата только его холдер и не пухнет. Impl-модуль отдаёт app'у публичные
`ChatModule`/`ChatInternalApi`.

## Поток данных

```
feature-*-impl                       app/di/chat                     feature-chat-impl
--------------                       -----------                     -----------------
ProfileChatWidgetsModule   ─┐
CatalogChatWidgetsModule   ─┼─ CompoundChatWidgetsModule ─┐
TransferChatWidgetsModule  ─┤       (только includes)     │
SettingsChatActionsModule  ─┘                             ▼
                                    ChatComponent (@PerFeature) ◄─ ChatModule (+@Multibinds-швы)
                                      реализует ChatInternalApi
                                              ▲
                                    ChatFeatureHolder (ленивый) ◄─ ChatHolderModule → AppComponent
                                              │                    (в корне ТОЛЬКО холдер)
                                    ChatWidgetRegistry (контейнер)
                                     │                        │
                              ChatFragment (XML)   ChatComposeFragment (Compose)
```

Сообщения: `FakeChatServer` (скрипт, Kotlin Flow) → `ChatRepositoryImpl`
(`StateFlow<List<ChatMessage>>`, @PerFeature — лента общая для обоих рендеров) →
`ChatInteractor` → `ChatViewModel` → экран. Виджет-сообщение несёт только `widgetId` + `payload`
(сырые строки) — кто рисует, решает карта; неизвестный id (сервер шлёт `GIBDD_FINES`) → заглушка.

## Контракты (feature-chat-api)

- `ChatWidgetViewHolderFactory` → `ChatWidgetViewHolder.bind(WidgetMessage)` — XML-рендер виджета;
- `ChatWidgetComposer.Content(WidgetMessage)` — Compose-рендер (тот же ключ, своя карта);
- `ChatQuickAction(id, title, order, isAvailable, onClick(FragmentActivity))` — чип с навигацией «с собой»;
- `@ChatWidgets` — qualifier-шов агрегатов; `ChatWidgetIds` — строковые server-driven ключи.

## Вклады фич

| Фича | Виджет (@IntoMap ×2) | Быстрое действие (@IntoSet) |
|---|---|---|
| profile | `PROFILE_CARD` — карточка (данные из СВОЕГО репозитория через `api<ProfileFeatureApi>()`) | «Профиль» |
| catalog | `CATALOG_SHOWCASE` — топ товаров, `limit` из payload | «Каталог» |
| transfer | `TRANSFER_ACTION` — кнопка запуска SDUI-флоу перевода | «Перевод» |
| settings | — (вклады независимы: Set-вклад не тянет Compose) | «Настройки» |

Демо: экран профиля → «Чат с ассистентом (XML/Compose)»; в чате — «профиль», «каталог»,
«перевод», «штраф» (фолбэк), «всё».

## Стратегии диспатча

- виджеты — **key-lookup** по server-driven ключу + **фолбэк** на промах;
- быстрые действия — **iterate-all** с фильтром `isAvailable()` и сортировкой `order`;
- view types адаптера — генерация типа по позиции id в реестре.
