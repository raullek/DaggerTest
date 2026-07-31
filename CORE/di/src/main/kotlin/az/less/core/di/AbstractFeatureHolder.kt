package az.less.core.di

/**
 * Общий предок всех холдеров: хранит [FeatureContainer] (через него фича достаёт чужие API) и задаёт
 * контракт [buildFeature]. Кэширования здесь НЕТ — стратегию жизни задают наследники
 * ([BaseFeatureHolder] — strong-кэш, [StatelessFeatureHolder] — без кэша).
 */
abstract class AbstractFeatureHolder<T : Any>(
    protected val container: FeatureContainer,
) : FeatureHolder<T> {

    /** Получить чужую зависимость (API другой фичи) из контейнера при построении своего графа. */
    protected fun <D : Any> getDependency(key: Class<D>): D = container.getDependency(key)

    /** Собрать Dagger-граф фичи и вернуть её публичный API. */
    protected abstract fun buildFeature(): T
}
