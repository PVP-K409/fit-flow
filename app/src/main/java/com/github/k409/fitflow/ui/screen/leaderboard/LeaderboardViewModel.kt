package com.github.k409.fitflow.ui.screen.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.User
import com.google.firebase.auth.FirebaseAuth
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
            val userList = finalList(users)
            LeaderboardUiState.Success(
                users = userList,
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

fun finalList(users: List<User>): List<User> {
    val sortedUsers = users.sortedByDescending { it.xp }.mapIndexed { index, user ->
        user.copy(rank = index + 1)
    }
    val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
    val currentUserIndex = sortedUsers.indexOfFirst { it.uid == currentUser }

    val firstFiveList = sortedUsers.take(5)

    return if (currentUserIndex < 5) {
        firstFiveList
    } else {
        val start = currentUserIndex - 2
        val end = minOf(currentUserIndex + 3, sortedUsers.size)
        val sublist = sortedUsers.subList(start, end)
        val filteredSublist = sublist.filter { it !in firstFiveList }
        firstFiveList + filteredSublist
    }
}
