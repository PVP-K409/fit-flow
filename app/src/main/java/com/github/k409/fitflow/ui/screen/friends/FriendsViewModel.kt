package com.github.k409.fitflow.ui.screen.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.FriendsRepository
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    private val searchText = _searchText.asStateFlow()

    private val _searchTextByName = MutableStateFlow("")
    private val searchTextByName = _searchTextByName.asStateFlow()

    private val _searchResultsByEmail = MutableStateFlow<List<User>>(emptyList())
    val searchResultsByEmail: StateFlow<List<User>> = _searchResultsByEmail.asStateFlow()

    private val _searchResultsByName = MutableStateFlow<List<User>>(emptyList())
    val searchResultsByName: StateFlow<List<User>> = _searchResultsByName.asStateFlow()

    init {
        viewModelScope.launch {
            searchText.collect { query ->
                if (query.length >= 3) {
                    _searchResultsByEmail.value = userRepository.searchUsersByEmail(query)
                } else {
                    _searchResultsByEmail.value = emptyList()
                }
            }
        }

        viewModelScope.launch {
            searchTextByName.collect { query ->
                if (query.length >= 3) {
                    _searchResultsByName.value = userRepository.searchUsersByName(query)
                } else {
                    _searchResultsByName.value = emptyList()
                }
            }
        }
    }

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

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }

    fun onSearchTextByNameChanged(text: String) {
        _searchTextByName.value = text
    }

    suspend fun searchUserByEmail(email: String): User {
        return userRepository.searchUserByEmail(email)
    }

    suspend fun searchUserByName(name: String): User {
        return userRepository.searchUserByName(name)
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

    fun getCurrentUser(): Flow<User> {
        return userRepository.currentUser
    }
}

sealed interface FriendsUiState {
    data object Loading : FriendsUiState
    data class Success(
        val friendRequests: List<Flow<User>>,
        val friends: List<Flow<User>>,
    ) : FriendsUiState
}
