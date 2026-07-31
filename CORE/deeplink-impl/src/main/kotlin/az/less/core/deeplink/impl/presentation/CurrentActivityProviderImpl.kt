package az.less.core.deeplink.impl.presentation

import android.app.Activity
import android.app.Application
import android.os.Bundle
import az.less.core.deeplink.api.CurrentActivityProvider
import az.less.core.deeplink.impl.di.DeeplinkScope
import az.less.core.deeplink.impl.view.DeeplinkActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Следит за текущей resumed-Activity через [Application.ActivityLifecycleCallbacks]
 * и публикует её в [StateFlow].
 *
 * [DeeplinkActivity] намеренно игнорируется: она — невидимый диспетчер, который
 * сразу завершится, хостом для экрана фичи быть не может.
 */
@DeeplinkScope
class CurrentActivityProviderImpl @Inject constructor() :
    CurrentActivityProvider,
    Application.ActivityLifecycleCallbacks {

    private val _activityFlow = MutableStateFlow<Activity?>(null)
    override val activityFlow: StateFlow<Activity?> = _activityFlow.asStateFlow()

    override val current: Activity? get() = _activityFlow.value

    override suspend fun awaitActivity(predicate: (Activity) -> Boolean): Activity =
        _activityFlow.filterNotNull().first(predicate)

    fun registerWith(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity is DeeplinkActivity) return
        _activityFlow.value = activity
    }

    override fun onActivityPaused(activity: Activity) {
        // Оставляем последнюю известную Activity как «текущую» до прихода следующей.
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (_activityFlow.value === activity) _activityFlow.value = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}
