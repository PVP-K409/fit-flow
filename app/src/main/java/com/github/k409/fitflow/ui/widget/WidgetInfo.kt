package com.github.k409.fitflow.ui.widget

import kotlinx.serialization.Serializable

@Serializable
sealed interface WidgetInfo {
    @Serializable
    data object Loading : WidgetInfo

    @Serializable
    data class Available(
        val waterLevel: Float = 0f,
        val healthLevel: Float = 0f,
        val steps: Long = 0,
        val calories: Long = 0,
        val distance: Double = 0.0,
        val hydration: Int = 0,
        val lastUpdated: String = "",
    ) : WidgetInfo

    @Serializable
    data class Unavailable(val message: String) : WidgetInfo
}
