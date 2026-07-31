package az.less.core.di

/**
 * Статический фасад доступа к фичам. Инициализируется один раз
 * в Application. Использовать напрямую — в Activity/Fragment, где DI нельзя
 * прокинуть в конструктор.
 */
object DI {

    @Volatile
    private var container: FeatureContainer? = null

    fun initialize(featureContainer: FeatureContainer) {
        container = featureContainer
    }

    fun <T : Any> getFeature(key: Class<T>): T {
        val c = container ?: error("DI is not initialized. Call DI.initialize() in Application.")
        return c.getFeature(key)
    }

    /**
     * Детерминированно освобождает граф очищаемой фичи (закрытие экрана, logout). Принимает только
     * `Class<out ReleasableApi>` — нечистимую фичу освободить нельзя by design.
     */
    fun releaseFeature(key: Class<out ReleasableApi>) {
        val c = container ?: error("DI is not initialized. Call DI.initialize() in Application.")
        c.releaseFeature(key)
    }
}

/** Kotlin-хелпер: `val api = api<ProfileFeatureApi>()`. */
inline fun <reified T : Any> api(): T = DI.getFeature(T::class.java)

/** Kotlin-хелпер: `releaseFeature<SomeReleasableApi>()`. */
inline fun <reified T : ReleasableApi> releaseFeature(): Unit = DI.releaseFeature(T::class.java)
