package az.less.core.di

/**
 * Контейнер инстанса одной фичи. Лениво создаёт её граф при первом обращении.
 */
interface FeatureHolder<out T : Any> {
    fun getFeature(): T

    /**
     * Зануляет кэш инстанса фичи (и каскадно — зависимые releasable-фичи). По умолчанию no-op:
     * base/core-холдеры не освобождаются (strong на весь процесс). Реальную реализацию даёт
     * [ReleasableFeatureHolder].
     */
    fun releaseFeature() {}
}
