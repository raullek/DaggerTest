package az.less.core.di

import javax.inject.Scope

/**
 * Scope всех фичевых API: объекты графа живут, пока жив инстанс фичи
 * (его кэширует [BaseFeatureHolder]).
 */
@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class PerFeature
