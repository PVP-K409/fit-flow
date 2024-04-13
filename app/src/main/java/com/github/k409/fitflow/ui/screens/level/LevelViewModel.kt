package com.github.k409.fitflow.ui.screens.level

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    userRepository: UserRepository,
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
}

sealed interface LevelUiState {
    data object Loading : LevelUiState
    data class Success(
        val user: User,
    ) : LevelUiState
}
