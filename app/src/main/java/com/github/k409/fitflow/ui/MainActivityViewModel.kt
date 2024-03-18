package com.github.k409.fitflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.UserRepository
import com.github.k409.fitflow.data.preferences.PreferencesRepository
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.theme.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userRepository: UserRepository,
    preferencesRepository: PreferencesRepository,
) : ViewModel() {

    val sharedUiState: StateFlow<SharedUiState> = combine(
        userRepository.currentUser,
        preferencesRepository.themeColourPreferences,
    ) { currentUser, themePreferences ->
        SharedUiState.Success(
            user = currentUser,
            themePreferences = themePreferences,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SharedUiState.Loading,
    )
}

sealed interface SharedUiState {
    data object Loading : SharedUiState
    data class Success(
        val user: User,
        val themePreferences: ThemePreferences,
    ) : SharedUiState
}
