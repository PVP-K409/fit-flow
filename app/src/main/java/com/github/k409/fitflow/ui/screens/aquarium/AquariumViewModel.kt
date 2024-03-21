package com.github.k409.fitflow.ui.screens.aquarium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.AquariumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AquariumViewModel @Inject constructor(
    private val aquariumRepository: AquariumRepository,
) : ViewModel() {

    val uiState: StateFlow<AquariumUiState> =
        aquariumRepository.get()
            .map {
                AquariumUiState.Success(it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AquariumUiState.Loading,
            )

    fun updateHealthLevel(level: Float) {
        val currentStats = uiState.value as? AquariumUiState.Success ?: return
        val newStats = currentStats.aquariumStats.copy(
            healthLevel = level.coerceIn(0f..1f)
        )

        viewModelScope.launch {
            aquariumRepository.update(newStats)
        }
    }

    fun updateWaterLevel(level: Float) {
        val currentStats = uiState.value as? AquariumUiState.Success ?: return
        val newStats = currentStats.aquariumStats.copy(
            waterLevel = level.coerceIn(0f..1f)
        )

        viewModelScope.launch {
            aquariumRepository.update(newStats)
        }
    }
}
