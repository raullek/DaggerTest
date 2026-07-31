package az.less.core.di

import kotlin.concurrent.withLock

/**
 * [BaseFeatureHolder], поддерживающий ДЕТЕРМИНИРОВАННОЕ освобождение графа фичи. [releaseFeature]
 * под локом зануляет кэш (следующий [getFeature] пересоберёт граф заново) и каскадно освобождает
 * зависимые releasable-фичи через [destroyDependencies].
 *
 * Освобождение НЕ происходит «само» по GC — его инициирует автор фичи (закрытие экрана, граница
 * сессии/logout) вызовом [DI.releaseFeature]. Если граф держит ресурсы (слушатели, корутины,
 * подписки), их нужно гасить в [destroyDependencies] (или в `dispose()` самого API).
 */
abstract class ReleasableFeatureHolder<T : ReleasableApi>(
    container: FeatureContainer,
) : BaseFeatureHolder<T>(container) {

    final override fun releaseFeature() {
        mBuildFeatureLock.withLock {
            mFeature = null
            destroyDependencies()
        }
    }

    /** Освободить зависимую releasable-фичу (вызывать из [destroyDependencies]). */
    protected fun releaseDependency(key: Class<out ReleasableApi>) {
        container.releaseFeature(key)
    }

    /**
     * Каскадное освобождение: переопредели и вызови [releaseDependency] для releasable-фич, от
     * которых зависит эта. По умолчанию — пусто (нет releasable-зависимостей). Здесь же гаси ресурсы.
     */
    protected open fun destroyDependencies() {}
}
