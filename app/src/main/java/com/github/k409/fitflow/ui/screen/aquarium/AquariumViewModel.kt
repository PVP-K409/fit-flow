package com.github.k409.fitflow.ui.screen.aquarium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.AquariumRepository
import com.github.k409.fitflow.data.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AquariumViewModel @Inject constructor(
    aquariumRepository: AquariumRepository,
    itemRepository: ItemRepository,
) : ViewModel() {

    val uiState: StateFlow<AquariumUiState> = combine(
        aquariumRepository.get(),
        itemRepository.getAquariumItems(),
    ) { stats, items ->
        AquariumUiState.Success(
            aquariumStats = stats,
            aquariumItems = items,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AquariumUiState.Loading,
        )
}
