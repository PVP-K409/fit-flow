package com.github.k409.fitflow.ui.screen.aquarium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.AquariumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AquariumViewModel @Inject constructor(
    aquariumRepository: AquariumRepository,
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
}
