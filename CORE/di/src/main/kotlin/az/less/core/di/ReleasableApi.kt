package az.less.core.di

/**
 * Маркер ОЧИЩАЕМОГО API фичи. Наследуют его только те API, граф которых можно детерминированно
 * сбросить через [DI.releaseFeature]/[FeatureContainer.releaseFeature].
 *
 * Тип-бонус: [DI.releaseFeature] принимает только `Class<out ReleasableApi>`, поэтому невозможно
 * случайно «освободить» нечистимую фичу — отсутствие маркера = гарантия, что API не сбрасывается.
 *
 * Сценарий: открыли экран → создали API фичи → поработали → при закрытии экрана почистили
 * (`DI.releaseFeature(...)`). Чистка ВСЕГДА инициируется автором фичи — «волшебного» GC-механизма нет.
 *
 * Ограничения releasable-фич:
 * - не живут в process-scope (prelogin/ядро);
 * - НЕ могут быть зависимостью НЕ-очищаемой фичи (иначе strong-холдер удержит мёртвый граф / получит
 *   рассинхрон). Releasable может зависеть только от releasable либо от process-scoped ядра.
 *
 * Холдер такой фичи расширяет [ReleasableFeatureHolder]; обычные (strong) — [BaseFeatureHolder].
 */
interface ReleasableApi : FeatureApi
