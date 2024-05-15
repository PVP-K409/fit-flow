package com.github.k409.fitflow.ui.screen.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.FriendsRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val friendsUiState: StateFlow<FriendsUiState> = combine(
        friendsRepository.getFriendRequests(),
        friendsRepository.getFriends(),
    ) { friendRequests, friends ->
        FriendsUiState.Success(
            friendRequests = friendRequests,
            friends = friends,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FriendsUiState.Loading,
    )

    suspend fun searchUser(email: String): User {
        return userRepository.searchUserByEmail(email)
    }

    suspend fun sendFriendRequest(uid: String) {
            friendsRepository.sendFriendRequest(uid)
    }

    suspend fun acceptFriendRequest(uid: String) {
            friendsRepository.acceptFriendRequest(uid)
    }

    suspend fun declineFriendRequest(uid: String) {
            friendsRepository.declineFriendRequest(uid)
    }

    suspend fun removeFriend(uid: String) {
            friendsRepository.removeFriend(uid)
    }
}

sealed interface FriendsUiState {
    data object Loading : FriendsUiState
    data class Success(
        val friendRequests: List<Flow<User>>,
        val friends: List<Flow<User>>,
    ) : FriendsUiState
}