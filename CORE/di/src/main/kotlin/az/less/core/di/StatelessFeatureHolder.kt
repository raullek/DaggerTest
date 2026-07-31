package az.less.core.di

/**
 * Холдер без memory-кэша: [getFeature] строит граф ЗАНОВО на каждый вызов. Подходит для дешёвых
 * stateless-фич, которые не нужно держать в памяти. [releaseFeature] no-op — кэша нет, инстанс
 * заберёт GC, как только уйдут ссылки у вызывающего.
 */
abstract class StatelessFeatureHolder<T : Any>(
    container: FeatureContainer,
) : AbstractFeatureHolder<T>(container) {

    final override fun getFeature(): T = buildFeature()
}
