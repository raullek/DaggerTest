package az.less.feature.chat.api.widget

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

/**
 * Достать [FragmentActivity] из Context вьюхи/композиции — нужно виджетам-вкладам,
 * чтобы дёрнуть лаунчер своей фичи по клику (навигация у виджета «с собой»).
 */
fun Context.findFragmentActivity(): FragmentActivity? {
    var current: Context = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) return current
        current = current.baseContext
    }
    return null
}
