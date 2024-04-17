package com.github.k409.fitflow.ui.screen.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.MarketRepository
import com.github.k409.fitflow.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    marketRepository: MarketRepository,
) : ViewModel() {

    val inventoryUiState: StateFlow<InventoryUiState> = combine(
        marketRepository.getUserOwnedItems(),
    ) { ownedItems ->
        InventoryUiState.Success(
            ownedItems = ownedItems[0],
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InventoryUiState.Loading,
    )
}
sealed interface InventoryUiState {
    data object Loading : InventoryUiState
    data class Success(
        val ownedItems: List<Item>,
    ) : InventoryUiState
}