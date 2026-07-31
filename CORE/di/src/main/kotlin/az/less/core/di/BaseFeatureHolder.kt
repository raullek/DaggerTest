package az.less.core.di

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Базовый холдер: лениво и потокобезопасно создаёт инстанс фичи в [buildFeature] и кэширует его
 * на ВЕСЬ процесс. [releaseFeature] здесь намеренно no-op (унаследован) — base-холдер не освобождает
 * граф (strong). Для сбрасываемых фич есть [ReleasableFeatureHolder].
 */
abstract class BaseFeatureHolder<T : Any>(
    container: FeatureContainer,
) : AbstractFeatureHolder<T>(container) {

    /** Lock, под которым создаётся/сбрасывается фича (общий с [ReleasableFeatureHolder]). */
    protected val mBuildFeatureLock = ReentrantLock()

    /** Кэш инстанса фичи. `protected`, чтобы [ReleasableFeatureHolder] мог его занулить. */
    @Volatile
    protected var mFeature: T? = null

    final override fun getFeature(): T {
        mFeature?.let { return it }
        return mBuildFeatureLock.withLock {
            mFeature ?: buildFeature().also { mFeature = it }
        }
    }
}
