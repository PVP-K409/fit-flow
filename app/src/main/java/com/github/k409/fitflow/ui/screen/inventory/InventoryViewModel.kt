package com.github.k409.fitflow.ui.screen.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.ItemRepository
import com.github.k409.fitflow.model.InventoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
) : ViewModel() {

    val inventoryUiState: StateFlow<InventoryUiState> =
        itemRepository.getUserOwnedItems().map { ownedItems ->
            InventoryUiState.Success(
                ownedItems = ownedItems,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InventoryUiState.Loading,
        )

    fun updateInventoryItem(inventoryItem: InventoryItem) {
        viewModelScope.launch {
            itemRepository.updateInventoryItem(inventoryItem)
        }
    }
}

sealed interface InventoryUiState {
    data object Loading : InventoryUiState
    data class Success(
        val ownedItems: List<InventoryItem>,
    ) : InventoryUiState
}
