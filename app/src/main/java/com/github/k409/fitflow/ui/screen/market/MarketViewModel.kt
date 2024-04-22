package com.github.k409.fitflow.ui.screen.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.ItemRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.InventoryItem
import com.github.k409.fitflow.model.MarketItem
import com.github.k409.fitflow.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val marketUiState: StateFlow<MarketUiState> = combine(
        userRepository.currentUser,
        itemRepository.getMarketItems(),
        itemRepository.getUserOwnedItems(),
    ) { user, items, ownedItems ->
        MarketUiState.Success(
            user = user,
            marketItems = items,
            ownedMarketItems = ownedItems,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MarketUiState.Loading,
    )

    fun updateUserCoinBalance(coinsAmount: Long) {
        viewModelScope.launch {
            userRepository.addCoinsAndXp(coinsAmount, 0)
        }
    }

    fun addItemToUserInventory(marketItem: MarketItem) {
        viewModelScope.launch {
            itemRepository.addItemToUserInventory(marketItem)
        }
    }

    fun removeItemFromUserInventory(marketItem: MarketItem) {
        viewModelScope.launch {
            itemRepository.removeItemFromUser(marketItem.id)
        }
    }
}

sealed interface MarketUiState {
    data object Loading : MarketUiState
    data class Success(
        val user: User,
        val marketItems: List<MarketItem>,
        val ownedMarketItems: List<InventoryItem>,
    ) : MarketUiState
}
