package com.github.k409.fitflow.ui.screen.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.R
import com.github.k409.fitflow.data.AuthRepository
import com.github.k409.fitflow.data.SignInResult
import com.github.k409.fitflow.service.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun loginWithGoogle() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val signInResult = authRepository.signInWithGoogle()

            showSignInStateMessage(signInResult)

            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun showSignInStateMessage(
        signInResult: SignInResult,
    ) {
        val message = when {
            signInResult.user != null -> context.getString(R.string.welcome, signInResult.user.name)
            signInResult.errorMessage != null -> signInResult.errorMessage
            else -> context.getString(R.string.sign_in_failed)
        }

        SnackbarManager.showMessage(message)
    }
}

data class LoginUiState(
    val isLoading: Boolean = false
)
