package com.github.k409.fitflow.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.AuthRepository
import com.github.k409.fitflow.data.UserRepository
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
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    val currentUser = userRepository.currentUser

    val settingsUiState: StateFlow<SettingsUiState> = combine(
        userRepository.currentUser,
        preferencesRepository.themeColourPreferences,
    ) { user, themePreferences ->
        SettingsUiState.Success(
            user = user,
            themePreferences = themePreferences,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState.Loading,
    )

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun updateThemePreferences(themePreferences: ThemePreferences) {
        viewModelScope.launch {
            preferencesRepository.updateThemePreferences(themePreferences)
        }
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val user: User,
        val themePreferences: ThemePreferences,
    ) : SettingsUiState
}
