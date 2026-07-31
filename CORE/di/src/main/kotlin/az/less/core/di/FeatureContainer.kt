package az.less.core.di

/**
 * Реестр фич: `Map<Class, FeatureHolder>`. Через него фичи получают чужие API.
 *
 * Два метода: [getFeature] — для использования в бизнес-логике,
 * [getDependency] — для получения чужого API при построении собственного графа
 * (точка, где в полноценной реализации отслеживаются циклические зависимости).
 */
interface FeatureContainer {
    fun <T : Any> getFeature(key: Class<T>): T
    fun <T : Any> getDependency(key: Class<T>): T

    /**
     * Детерминированно освобождает граф очищаемой фичи (зануляет кэш её холдера; следующий
     * [getFeature] пересоберёт). Тип-бонус `Class<out ReleasableApi>` не даёт освободить нечистимую
     * фичу. Если фича не зарегистрирована — no-op.
     */
    fun releaseFeature(key: Class<out ReleasableApi>)
}
