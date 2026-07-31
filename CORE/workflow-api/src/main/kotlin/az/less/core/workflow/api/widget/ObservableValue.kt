package az.less.core.workflow.api.widget

/**
 * Двусторонне-наблюдаемое значение поля.
 *
 * Две раздельные группы подписчиков ломают циклы «UI ↔ модель»:
 * - [updateFromUi] (значение пришло от пользователя) уведомляет [observe] (модель);
 * - [update] (значение задано программно/моделью) уведомляет [observeUi] (вьюха).
 *
 * Так EditText не зацикливается, перезаписывая сам себя при программном `update`.
 */
class ObservableValue<T>(initial: T) {

    private var current: T = initial
    private val uiListeners = mutableListOf<(T) -> Unit>()
    private val modelListeners = mutableListOf<(T) -> Unit>()

    fun get(): T = current

    /** Значение от пользователя → уведомляем модель. */
    fun updateFromUi(value: T) {
        current = value
        modelListeners.toList().forEach { it(value) }
    }

    /** Значение от модели/программно → уведомляем вьюху. */
    fun update(value: T) {
        current = value
        uiListeners.toList().forEach { it(value) }
    }

    /** Подписка модели на пользовательские изменения. */
    fun observe(listener: (T) -> Unit) {
        modelListeners += listener
    }

    /** Подписка вьюхи на программные изменения. */
    fun observeUi(listener: (T) -> Unit) {
        uiListeners += listener
    }
}
