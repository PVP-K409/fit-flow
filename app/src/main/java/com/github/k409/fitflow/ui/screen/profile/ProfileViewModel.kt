package com.github.k409.fitflow.ui.screen.profile

import androidx.lifecycle.ViewModel
import com.github.k409.fitflow.data.ProfileRepository
import com.github.k409.fitflow.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    userRepository: UserRepository,
) : ViewModel() {

    val currentUser = userRepository.currentUser

    suspend fun submitProfile(
        uid: String,
        name: String,
        dateOfBirth: String,
        gender: String,
        weight: Int,
        height: Int,
    ): Boolean {
        profileRepository.submitProfile(
            uid,
            name,
            dateOfBirth,
            gender,
            weight,
            height,
        )

        return profileRepository.success
    }
}
