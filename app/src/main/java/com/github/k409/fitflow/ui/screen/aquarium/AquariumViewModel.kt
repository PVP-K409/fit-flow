package com.github.k409.fitflow.ui.screen.aquarium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.AquariumRepository
import com.github.k409.fitflow.data.ItemRepository
import com.github.k409.fitflow.data.preferences.PreferenceKeys
import com.github.k409.fitflow.data.preferences.PreferencesRepository
import com.github.k409.fitflow.model.AquariumStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AquariumViewModel @Inject constructor(
    aquariumRepository: AquariumRepository,
    val preferencesRepository: PreferencesRepository,
    itemRepository: ItemRepository,
) : ViewModel() {

    val uiState: StateFlow<AquariumUiState> = combine(
        aquariumRepository.getAquariumStats(),
        preferencesRepository.getPreference(PreferenceKeys.WATER_LEVEL, 0.25f),
        preferencesRepository.getPreference(PreferenceKeys.HEALTH_LEVEL, 0.25f),
        itemRepository.getAquariumItems(),
    ) { stats, waterLevel, healthLevel, items ->
        AquariumUiState.Success(
            aquariumStats = stats,
            localAquariumStats = AquariumStats(
                waterLevel = waterLevel,
                healthLevel = healthLevel,
            ),
            aquariumItems = items,
            fishes = items.filter { it.item.type == "fish" },
            decorations = items.filter { it.item.type == "decoration" },
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AquariumUiState.Loading,
        )

    suspend fun updateLocalAquariumStats(stats: AquariumStats) {
        preferencesRepository.putPreference(PreferenceKeys.WATER_LEVEL, stats.waterLevel)
        preferencesRepository.putPreference(PreferenceKeys.HEALTH_LEVEL, stats.healthLevel)
    }
}
