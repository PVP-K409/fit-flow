package com.github.k409.fitflow.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.github.k409.fitflow.data.AuthRepository
import com.github.k409.fitflow.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    suspend fun signOut() {
        authRepository.signOut()
    }

    val currentUser = userRepository.getCurrentUser
}
