package com.github.k409.fitflow.ui.screen.aquarium

import com.github.k409.fitflow.model.AquariumStats
import com.github.k409.fitflow.model.InventoryItem

sealed interface AquariumUiState {
    data object Loading : AquariumUiState
    data class Success(
        val aquariumStats: AquariumStats,
        val localAquariumStats: AquariumStats,
        val aquariumItems: List<InventoryItem>,
        val fishes: List<InventoryItem>,
        val decorations: List<InventoryItem>,
    ) : AquariumUiState
}
