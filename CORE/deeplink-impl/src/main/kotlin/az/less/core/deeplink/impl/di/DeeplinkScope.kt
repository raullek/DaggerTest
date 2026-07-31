package az.less.core.deeplink.impl.di

import javax.inject.Scope

/**
 * Scope графа ядра диплинков: одна копия фасада, хранилища, провайдера Activity
 * и т.д. на инстанс фичи.
 */
@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class DeeplinkScope
