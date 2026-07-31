package az.less.core.di

/**
 * Реализация реестра. Создаётся пустой, затем [init] получает мапу холдеров
 * (которая сама ссылается на этот контейнер — так разрывается циклическая
 * зависимость «контейнер ↔ холдеры»).
 */
class FeatureContainerImpl : FeatureContainer {

    private var holders: Map<Class<*>, FeatureHolder<*>> = emptyMap()

    fun init(holdersProvider: (FeatureContainer) -> Map<Class<*>, FeatureHolder<*>>): FeatureContainerImpl {
        holders = holdersProvider(this)
        return this
    }

    override fun <T : Any> getFeature(key: Class<T>): T = resolve(key)

    override fun <T : Any> getDependency(key: Class<T>): T = resolve(key)

    override fun releaseFeature(key: Class<out ReleasableApi>) {
        // Нет холдера — фича не зарегистрирована/уже недоступна: тихо игнорируем.
        holders[key]?.releaseFeature()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> resolve(key: Class<T>): T {
        val holder = holders[key]
            ?: error("No FeatureHolder registered for ${key.name}")
        return holder.getFeature() as T
    }
}
