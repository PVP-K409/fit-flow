package com.github.k409.fitflow.ui.screen.aquarium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.AquariumRepository
import com.github.k409.fitflow.data.MarketRepository
import com.github.k409.fitflow.model.AquariumStats
import com.github.k409.fitflow.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AquariumViewModel @Inject constructor(
    aquariumRepository: AquariumRepository,
    private val marketRepository: MarketRepository,
) : ViewModel() {

    val uiState: StateFlow<AquariumUiState> = combine(
        aquariumRepository.get(),
        getAquariumItems(),
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
    // TODO : filter out items, that are not placed in aquarium
    private fun getAquariumItems(): Flow<List<Item>> {
        return marketRepository.getUserOwnedItems()
    }
}
