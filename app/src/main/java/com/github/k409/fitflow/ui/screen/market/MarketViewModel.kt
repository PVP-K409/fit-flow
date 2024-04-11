package com.github.k409.fitflow.ui.screen.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.MarketRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.Item
import com.github.k409.fitflow.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val marketRepository: MarketRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val marketUiState: StateFlow<MarketUiState> = combine(
        userRepository.currentUser,
        marketRepository.getMarketItems(),
        marketRepository.getUserOwnedItems(),
    ) { user, items, ownedItems ->
        MarketUiState.Success(
            user = user,
            items = items,
            ownedItems = ownedItems,
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
    fun addItemToUserInventory(item: Item) {
        viewModelScope.launch {
            marketRepository.addItemToUser(item)
        }
    }
    fun removeItemFromUserInventory(item: Item) {
        viewModelScope.launch {
            marketRepository.removeItemFromUser(item)
        }
    }
    suspend fun getImageDownloadUrl(imageUrl: String): String {
        var url: String
        withContext(Dispatchers.IO) {
            url = marketRepository.getImageDownloadUrl(imageUrl)
        }
        return url
    }
}
sealed interface MarketUiState {
    data object Loading : MarketUiState
    data class Success(
        val user: User,
        val items: List<Item>,
        val ownedItems: List<Item>,
    ) : MarketUiState
}
