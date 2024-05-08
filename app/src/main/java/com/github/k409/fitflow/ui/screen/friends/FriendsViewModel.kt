package com.github.k409.fitflow.ui.screen.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.FriendsRepository
import com.github.k409.fitflow.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
) : ViewModel() {

    val friendsUiState: StateFlow<FriendsUiState> = combine(
        friendsRepository.getFriendsDetails(),
        friendsRepository.getFriendRequestsDetails(),
    ) { friends, friendRequests ->
        FriendsUiState.Success(
            friends = friends,
            friendRequests = friendRequests,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FriendsUiState.Loading,
    )

    suspend fun sendFriendRequest(uid: String){
            friendsRepository.sendFriendRequest(uid)
    }

    suspend fun acceptFriendRequest(uid: String) {
            friendsRepository.acceptFriendRequest(uid)
    }

    suspend fun declineFriendRequest(uid: String) {
            friendsRepository.declineFriendRequest(uid)
    }
}

sealed interface FriendsUiState {
    data object Loading : FriendsUiState
    data class Success(
        val friends: List<User>,
        val friendRequests: List<User>,
    ) : FriendsUiState
}