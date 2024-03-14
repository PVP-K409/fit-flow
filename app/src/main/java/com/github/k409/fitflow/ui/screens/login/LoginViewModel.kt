package com.github.k409.fitflow.ui.screens.login

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import com.github.k409.fitflow.data.AuthRepository
import com.github.k409.fitflow.data.SignInResult
import com.github.k409.fitflow.ui.screens.hydration.HydrationReminder
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    suspend fun firebaseAuthWithGoogle(
        context: Context,
        idToken: String,
    ) {
        val signInResult = authRepository.firebaseAuthWithGoogle(idToken)

        showSignInStateToast(context, signInResult)
    }

    fun signInWithGoogle(
        signInLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
        googleSignInClient: GoogleSignInClient,
    ) {
        authRepository.signInWithGoogle(signInLauncher, googleSignInClient)
    }

    private fun showSignInStateToast(
        context: Context,
        signInResult: SignInResult,
    ) {
        if (signInResult.user != null) {
            Toast.makeText(context, "Welcome, ${signInResult.user.name}", Toast.LENGTH_SHORT)
                .show()

            HydrationReminder().scheduleWaterReminder(context)

            return
        }

        if (signInResult.errorMessage != null) {
            Toast.makeText(context, signInResult.errorMessage, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Sign in failed", Toast.LENGTH_SHORT).show()
        }
    }
}
