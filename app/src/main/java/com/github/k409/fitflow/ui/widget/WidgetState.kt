package com.github.k409.fitflow.ui.widget

import kotlinx.serialization.Serializable


@Serializable
sealed interface WidgetInfo {
    @Serializable
    data object Loading : WidgetInfo

    @Serializable
    data class Available(
        val waterLevel: Float,
        val healthLevel: Float,
        val steps: Long,
        val calories: Long,
        val distance: Double,
        val hydration: Int,
    ) : WidgetInfo

    @Serializable
    data class Unavailable(val message: String) : WidgetInfo
}