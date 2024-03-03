package com.github.k409.fitflow.di.annotations

import javax.inject.Qualifier

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IODispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainImmediateDispatcher
