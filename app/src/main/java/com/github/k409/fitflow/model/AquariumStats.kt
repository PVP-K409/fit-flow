package com.github.k409.fitflow.model

data class AquariumStats(
    val waterLevel: Float = 0.85f,
    val healthLevel: Float = 1.0f,
    val fish: Fish = Fish.Primary,
)