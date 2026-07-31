// Демонстрация: ПОЧЕМУ инвариантность по умолчанию, и что ломается.
// Все эти строки ДОЛЖНЫ не скомпилироваться — смотрим, что скажет компилятор.

// Доменные типы: фичи (как в проекте)
interface FeatureApi
interface ReleasableApi : FeatureApi
class TransferApi : ReleasableApi          // конкретная releasable-фича
class NetworkApi : FeatureApi              // НЕ releasable (нет маркера)

// --- 1. Инвариантный контейнер (без out/in) ---
class Box<T>(val value: T)

fun case1() {
    val transfer: Box<TransferApi> = Box(TransferApi())
    val any: Box<FeatureApi> = transfer        // ❌ ОШИБКА: Box инвариантен
}

// --- 2. Class<T> без out: нельзя передать класс наследника ---
fun release(key: Class<ReleasableApi>) {}

fun case2() {
    release(TransferApi::class.java)           // ❌ ОШИБКА: Class<TransferApi> != Class<ReleasableApi>
}

// --- 3. out-тип нельзя принимать внутрь (как параметр) ---
interface Producer<out T> {
    fun produce(): T
    fun consume(item: T)                       // ❌ ОШИБКА: T в позиции in у out-параметра
}

// --- 4. Типобезопасность маркера: нерелизуемую фичу освободить нельзя ---
fun releaseOk(key: Class<out ReleasableApi>) {}

fun case4() {
    releaseOk(NetworkApi::class.java)          // ❌ ОШИБКА: NetworkApi не ReleasableApi
}
