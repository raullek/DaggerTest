// Рабочее демо вариантности — мини-копия вашего DI. Компилируется и запускается.

interface FeatureApi
interface ReleasableApi : FeatureApi

class TransferApi : ReleasableApi { fun pay() = println("    TransferApi.pay()") }
class NetworkApi : FeatureApi                       // НЕ releasable

// ============ 1. out = producer (ковариантность) ============
// Holder только ОТДАЁт T (getFeature), значит out безопасен.
interface Holder<out T : Any> { fun getFeature(): T }

// ============ 2. in = consumer (контравариантность) ============
// Logger только ПРИНИМАЕТ T, значит in безопасен.
interface Logger<in T> { fun log(item: T) }

// ============ 3. Мини-контейнер с releaseFeature(Class<out ReleasableApi>) ============
abstract class BaseHolder<T : Any> : Holder<T> {
    protected var mFeature: T? = null
    final override fun getFeature(): T = mFeature ?: build().also {
        mFeature = it; println("  [build] ${it!!::class.simpleName}")
    } ?: error("")
    open fun releaseFeature() {}                     // strong: no-op
    abstract fun build(): T
}

abstract class ReleasableHolder<T : ReleasableApi> : BaseHolder<T>() {
    final override fun releaseFeature() {
        println("  [release] кэш занулён")
        mFeature = null
    }
}

class TransferHolder : ReleasableHolder<TransferApi>() {
    override fun build() = TransferApi()
}
class NetworkHolder : BaseHolder<NetworkApi>() {     // strong
    override fun build() = NetworkApi()
}

class Container {
    // ⭐ звёздная проекция: ключи и значения РАЗНЫХ типов
    private val holders: Map<Class<*>, BaseHolder<*>> = mapOf(
        TransferApi::class.java to TransferHolder(),
        NetworkApi::class.java to NetworkHolder(),
    )
    fun <T : Any> getFeature(key: Class<T>): T = holders.getValue(key).getFeature() as T

    // принимает ТОЛЬКО Class<out ReleasableApi> — нерелизуемую не передать (проверено в demo_errors)
    fun releaseFeature(key: Class<out ReleasableApi>) = holders[key]?.releaseFeature()
}

fun main() {
    // 1. out в деле: Holder<TransferApi> присваивается в Holder<FeatureApi>
    val th: Holder<TransferApi> = TransferHolder()
    val fh: Holder<FeatureApi> = th                 // ✅ ковариантность
    println("out: Holder<TransferApi> -> Holder<FeatureApi> = ${fh.javaClass.simpleName}")

    // 2. in в деле: Logger<Any> присваивается в Logger<TransferApi>
    val anyLogger: Logger<Any> = object : Logger<Any> { override fun log(item: Any) = println("    log: $item") }
    val tLogger: Logger<TransferApi> = anyLogger    // ✅ контравариантность
    tLogger.log(TransferApi())

    // 3. жизненный цикл через releaseFeature(Class<out ReleasableApi>)
    val c = Container()
    println("\n--- первый доступ (строится + кэшируется) ---")
    val t1 = c.getFeature(TransferApi::class.java); t1.pay()
    println("--- второй доступ (из кэша, build НЕ печатается) ---")
    val t2 = c.getFeature(TransferApi::class.java); t2.pay()
    println("тот же инстанс? ${t1 === t2}")

    println("\n--- release(TransferApi) — можно, т.к. : ReleasableApi ---")
    c.releaseFeature(TransferApi::class.java)
    println("--- следующий доступ пересоберёт ---")
    val t3 = c.getFeature(TransferApi::class.java)
    println("новый инстанс после release? ${t1 !== t3}")
}
