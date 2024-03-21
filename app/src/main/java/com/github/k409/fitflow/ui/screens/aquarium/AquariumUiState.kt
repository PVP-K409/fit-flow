package com.github.k409.fitflow.ui.screens.aquarium

import androidx.annotation.FloatRange
import com.github.k409.fitflow.model.AquariumFish
import com.github.k409.fitflow.model.FishPhase
import com.github.k409.fitflow.model.FishType

data class AquariumUiState(
    val fish: AquariumFish = AquariumFish(type = FishType.PrimaryFish, phase = FishPhase.Regular),
    @FloatRange(from = 0.0, to = 1.0) val waterLevel: Float = 0.85f,
    @FloatRange(from = 0.0, to = 1.0) val healthLevel: Float = 1.0f,
)
