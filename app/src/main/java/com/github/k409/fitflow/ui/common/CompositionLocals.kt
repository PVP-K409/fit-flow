package com.github.k409.fitflow.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    noLocalProvidedFor("LocalSnackbarHostState")
}

fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}