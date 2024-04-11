package com.github.k409.fitflow.ui.screen.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.MarketRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.Item
import com.github.k409.fitflow.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    marketRepository: MarketRepository,
    userRepository: UserRepository,
) : ViewModel() {
    //val currentUser = userRepository.currentUser

    val marketUiState: StateFlow<MarketUiState> = combine (
        userRepository.currentUser,
        marketRepository.getAllItems(),
    )
     { user, items ->
        MarketUiState.Success(
            user = user,
            items = items,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MarketUiState.Loading,
    )
}
sealed interface MarketUiState {
    data object Loading : MarketUiState
    data class Success(
        val user: User,
        val items: List<Item>,
    ) : MarketUiState
}