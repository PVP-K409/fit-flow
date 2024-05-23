package com.github.k409.fitflow.model

import kotlinx.serialization.Serializable

@Serializable
data class AquariumStats(
    val waterLevel: Float = 0.85f,
    val healthLevel: Float = 1.0f,
)
