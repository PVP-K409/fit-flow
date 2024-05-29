package com.github.k409.fitflow.model

import kotlinx.serialization.Serializable

@Serializable
data class AquariumStats(
    val waterLevel: Float = 1.0f,
    val healthLevel: Float = 1.0f,
)
