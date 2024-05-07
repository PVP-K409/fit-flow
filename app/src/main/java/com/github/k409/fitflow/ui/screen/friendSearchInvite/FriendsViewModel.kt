package com.github.k409.fitflow.ui.screen.friendSearchInvite

import androidx.lifecycle.ViewModel
import com.github.k409.fitflow.data.FriendsRepository
import com.github.k409.fitflow.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    userRepository: UserRepository,
) : ViewModel() {

    val currentUser = userRepository.currentUser

    val friendsUID = friendsRepository.getFriendsUID()

    suspend fun sendFriendRequest(uid: String){
        friendsRepository.sendFriendRequest(uid)
        //return friendsRepository.success
    }

    suspend fun acceptFriendRequest(uid: String) {
        friendsRepository.acceptFriendRequest(uid)
        //return friendsRepository.success
    }

    suspend fun deleteFriendRequest(uid: String) {
        friendsRepository.deleteFriendRequest(uid)
        //return friendsRepository.success
    }
}