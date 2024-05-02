package com.github.k409.fitflow.ui.screen.level

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.ItemRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.MarketItem
import com.github.k409.fitflow.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LevelUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository,
) : ViewModel() {
    val levelUpUiState: StateFlow<LevelUpUiState> = combine(
        userRepository.currentUser,
        itemRepository.getRewardItems(),
    ) { user, rewards ->
        LevelUpUiState.Success(
            user = user,
            rewards = rewards,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LevelUpUiState.Loading,
    )

    suspend fun updateUserField(field: String, value: Any) {
        userRepository.updateUserField(field, value)
    }

    // User level needs to be incremented by 1000, because reward numeration starts from 1000
    suspend fun addRewardItemToUserInventory(userLevel: Int) {
        itemRepository.addRewardItemToUserInventory(userLevel + 1000)
    }
}

sealed interface LevelUpUiState {
    data object Loading : LevelUpUiState
    data class Success(
        val user: User,
        val rewards: List<MarketItem>,
    ) : LevelUpUiState
}
