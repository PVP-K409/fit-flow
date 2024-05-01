package com.github.k409.fitflow.ui.screen.level

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.ItemRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LevelViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository,
) : ViewModel() {
    val levelUiState: StateFlow<LevelUiState> =
        userRepository.currentUser.map { user ->
            LevelUiState.Success(
                user = user,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LevelUiState.Loading,
        )

    suspend fun updateUserField(field: String, value: Any) {
        userRepository.updateUserField(field, value)
    }
    suspend fun addRewardItemToUserInventory(userLevel : Int) {
        itemRepository.addRewardItemToUserInventory(userLevel)
    }
}

sealed interface LevelUiState {
    data object Loading : LevelUiState
    data class Success(
        val user: User,
    ) : LevelUiState
}
