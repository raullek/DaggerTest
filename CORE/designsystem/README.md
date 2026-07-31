# core-designsystem

Leaf UI-библиотека проекта (как `material`): единые токены (цвет/отступы/скругление),
набор переиспользуемых компонентов в XML и инфлейтер по семантическому ключу. Без DI и
`FeatureHolder` — от неё зависят напрямую.

## Что внутри

- **Токены** (`res/values/`): `colors.xml` (`ds_primary`, `ds_surface`, `ds_on_surface`,
  `ds_outline`, `ds_error`, `ds_hint`), `dimens.xml` (`ds_space_*`, `ds_corner`,
  `ds_field_height`), `styles.xml` (`Ds.Label`, `Ds.Error`, `Ds.SectionTitle`,
  `Ds.TextInput`, `Ds.Button.Primary`, `Ds.Card`, …).
- **Компоненты** (`res/layout/`, со стабильными id):

  | Ключ `DsComponent` | Лейаут | Ключевые id |
  |---|---|---|
  | `TEXT_FIELD` / `DATE_FIELD` | `ds_text_field.xml` | `ds_label`, `ds_input`, `ds_error` |
  | `MONEY_FIELD` | `ds_money_field.xml` | `ds_label`, `ds_input`, `ds_suffix`, `ds_error` |
  | `SELECT_FIELD` | `ds_select_field.xml` | `ds_label`, `ds_value`, `ds_error` |
  | `CHECKBOX_FIELD` | `ds_checkbox_field.xml` | `ds_checkbox`, `ds_error` |
  | `BUTTON` | `ds_button.xml` | `ds_action` |
  | `SECTION_HEADER` | `ds_section_header.xml` | `ds_title`, `ds_subtitle` |
  | `CARD` | `ds_card.xml` | `ds_container` |

- **API** (Kotlin):
  - `DsComponent` — enum ключей + `from(key: String?)` (выбор по строке, в т.ч. server-driven).
  - `DesignSystemInflater.inflate(context, component, parent?, attach?)` — ключ → готовая View.
  - `DsViews` — `View.dsLabel()/dsInput()/dsError()/dsValue()/dsCheckbox()/dsSuffix()/dsTitle()/dsSubtitle()/dsAction()/dsContainer()`.

## Как пользоваться

```kotlin
val view = DesignSystemInflater.inflate(context, DsComponent.TEXT_FIELD)
view.dsLabel().text = "ФИО"
view.dsInput().addTextChangedListener { /* ... */ }
```

Первый потребитель — рендереры `:core-workflow-impl` (server-driven UI инфлейтит компоненты
по типу/`style` поля). См. `CORE/workflow-impl/WORKFLOW.md`.

## Добавить компонент

1. Новый `ds_<name>.xml` в `res/layout/` со стабильными id (переиспользуй существующие id,
   если роль та же).
2. Новое значение в `DsComponent` + ветка в `DesignSystemInflater.layoutOf`.
3. При необходимости — новый хелпер в `DsViews` и стиль в `styles.xml`.
