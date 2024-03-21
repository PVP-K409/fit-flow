package com.github.k409.fitflow.ui.screens.aquarium

import androidx.lifecycle.ViewModel
import com.github.k409.fitflow.model.AquariumFish
import com.github.k409.fitflow.model.FishPhase
import com.github.k409.fitflow.model.FishType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AquariumViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AquariumUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAquarium()
    }

    private fun loadAquarium() {
        val healthLevel = 1.0f
        val waterLevel = 0.85f

        _uiState.update { uiState ->
            uiState.copy(
                fish = AquariumFish(
                    type = FishType.ThirdFish,
                    phase = FishPhase.getPhase(healthLevel)
                ),
                waterLevel = waterLevel,
                healthLevel = healthLevel
            )
        }
    }

    fun onHealthLevelChanged(it: Float) {
        _uiState.update { uiState ->
            val newPhase = FishPhase.getPhase(it)
            val currentFish = uiState.fish

            uiState.copy(healthLevel = it, fish = currentFish.copy(phase = newPhase))
        }
    }

    fun onWaterLevelChanged(it: Float) {
        _uiState.update { uiState ->
            uiState.copy(waterLevel = it)
        }
    }

    fun onFishChanged(it: AquariumFish) {
        _uiState.update { uiState ->
            val currentFish = uiState.fish

            uiState.copy(fish = currentFish.copy(phase = it.phase))
        }
    }

}
