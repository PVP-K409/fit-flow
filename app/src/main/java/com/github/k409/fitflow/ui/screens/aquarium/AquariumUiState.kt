package com.github.k409.fitflow.ui.screens.aquarium

import com.github.k409.fitflow.model.AquariumStats

sealed interface AquariumUiState {
    data object Loading : AquariumUiState
    data class Success(
        val aquariumStats: AquariumStats,
    ) : AquariumUiState
}
