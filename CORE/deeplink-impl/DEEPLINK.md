# Deeplink mechanism

This module pair (`core-deeplink-api` / `core-deeplink-impl`) implements deeplink
handling for this project's multimodular `FeatureHolder` DI registry, on
coroutines.

## 1. Architecture

The pair is an `api/impl` library with a **chain-of-responsibility step
pipeline** dispatched through Dagger multibindings:

1. **Entry** — one `DeeplinkActivity` catches every `VIEW` intent (intent-filters
   for the internal `app-app://` and external `daggertest://` schemes). It is a
   `singleTask` `noHistory` translucent dispatcher that hands the `Uri` to the
   core and finishes.
2. **Facade** — `DeeplinkFacadeImpl.handle()` validates the Uri, then asks
   `DeeplinkHandlerStorage` for a handler.
3. **Matching** — `DeeplinkHandlerStorage` keys handlers by a normalized
   canonical token key (query stripped, schemes unified) with **nested-path**
   matching; a `Set<DeeplinkUriMatcher>` is the dynamic fallback.
4. **Registration (the DI heart)** — feature modules contribute handlers without
   the core knowing them, via Dagger:
   ```kotlin
   @Provides @IntoSet
   fun provideCatalogEntry(): DeeplinkHandlerEntry =
       DeeplinkHandlerEntry(Deeplinks.feature("catalog")) {
           factory.handlerBuilder().addStep(...).build()
       }
   ```
   `handlerProvider` is a lazy `() -> DeeplinkHandler` — the handler graph is
   built only on a match. The `AppComponent` aggregates every `@IntoSet` across
   all feature modules into one `Set<DeeplinkHandlerEntry>`.
5. **Step pipeline** — `AndroidDeeplinkStepsRunner` runs an ordered
   `List<DeeplinkStep>` sequentially, short-circuiting on the first `Failed`.
   Steps: `ConditionDeeplinkStep`, `PredicateDeeplinkStep`,
   `FilterSourceDeeplinkStep`, `LaunchFeatureDeeplinkStep`. A failed step that
   can't self-handle routes to a `FailedResultHandler`. Every `execute()` is
   `suspend` — no Rx anywhere.
6. **Scope** — `@DeeplinkScope` keeps one instance of the facade/storage/etc.
   per feature instance; `CurrentActivityProvider` (backed by
   `ActivityLifecycleCallbacks`, exposed as a `StateFlow`) gives steps a live
   Activity to start screens from. `attach(application)` is called once from
   `App`.

### How multibinding aggregation works here

Every feature builds its **own** Dagger component inside its `FeatureHolder`, so
multibindings can't cross components. The **`AppComponent` is the single
component that sees every feature** (it includes all `…HolderModule`s). So each
feature contributes its `@IntoSet DeeplinkHandlerEntry` through a small module
(`CatalogDeeplinkModule`, …) that `AppHolderModule` includes; Dagger aggregates
them there and hands the `Set` to the deeplink graph via `@BindsInstance`. Each
entry's lazy lambda reaches the contributing feature through the global `DI`
registry, so the deeplink core still depends on **no** feature.

## 2. Flow: tap link → feature screen

1. Android matches a `<intent-filter>` → launches `DeeplinkActivity`.
2. It builds a `DeeplinkUri`, starts the app's launcher Activity (host) and calls
   `router().open(uri)`, then finishes.
3. The router runs `DeeplinkFacadeImpl.handle()` **on the core's own scope** (so it
   survives the dispatcher closing).
4. `DeeplinkHandlerStorage` finds the matching `@IntoSet` entry and lazily builds
   its `DeeplinkHandler`.
5. `AndroidDeeplinkStepsRunner` runs the steps; the terminal
   `LaunchFeatureDeeplinkStep` awaits the host Activity (via
   `CurrentActivityProvider`) and calls the feature's own `Launcher`.

## 3. Try it

```
# external scheme
adb shell am start -a android.intent.action.VIEW -d "daggertest://catalog"
adb shell am start -a android.intent.action.VIEW -d "daggertest://profile"
adb shell am start -a android.intent.action.VIEW -d "daggertest://settings?ok=true"   # condition step passes
adb shell am start -a android.intent.action.VIEW -d "daggertest://settings"            # condition fails → no nav

# internal scheme
adb shell am start -a android.intent.action.VIEW -d "app-app://az.less.daggertest/catalog"
```

Add a new deeplink to a feature: create an `XxxDeeplinkModule` with one
`@Provides @IntoSet DeeplinkHandlerEntry`, and add it to `AppHolderModule.includes`.
Nothing in the core changes.
