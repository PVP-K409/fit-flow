package com.github.k409.fitflow.ui.screen.leaderboard

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
class LeaderboardViewModel @Inject constructor(
    userRepository: UserRepository,
) : ViewModel() {
    val leaderboardUiState: StateFlow<LeaderboardUiState> =
        userRepository.getAllUserProfiles().map { users ->
            val sortedUsers = users.sortedByDescending { it.xp }
            LeaderboardUiState.Success(
                users = sortedUsers,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LeaderboardUiState.Loading,
        )
}

sealed interface LeaderboardUiState {
    data object Loading : LeaderboardUiState
    data class Success(
        val users: List<User>,
    ) : LeaderboardUiState
}