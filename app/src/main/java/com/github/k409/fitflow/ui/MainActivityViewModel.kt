package com.github.k409.fitflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.data.preferences.PreferenceKeys
import com.github.k409.fitflow.data.preferences.PreferencesRepository
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.theme.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    val sharedUiState: StateFlow<SharedUiState> = combine(
        userRepository.currentUser,
        preferencesRepository.themeColourPreferences,
    ) { currentUser, themePreferences ->
        SharedUiState.Success(
            user = currentUser,
            themePreferences = themePreferences,
            sharedPreferences = preferencesRepository,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SharedUiState.Loading,
    )

    init {
        observeUserLogin()
    }

    private fun observeUserLogin() {
        viewModelScope.launch {
            sharedUiState.collect {
                val state = it as? SharedUiState.Success ?: return@collect
                val user = state.user

                if (user.uid.isNotEmpty()) {
                    preferencesRepository.getPreference(PreferenceKeys.FCM_TOKEN, "")
                        .collect { token ->
                            if (token.isNotEmpty() && token != user.fcmToken) {
                                userRepository.updateFcmToken(token)
                            }
                        }
                }
            }
        }
    }

    fun isLoading(): Boolean {
        return sharedUiState.value is SharedUiState.Loading
    }
}

sealed interface SharedUiState {
    data object Loading : SharedUiState
    data class Success(
        val user: User,
        val themePreferences: ThemePreferences,
        val sharedPreferences: PreferencesRepository,
    ) : SharedUiState
}
