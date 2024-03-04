package com.github.k409.fitflow.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.toUser
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class SignInResult(
    val user: User?,
    val errorMessage: String?
)

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) {
    fun signInWithGoogle(
        signInLauncher: ActivityResultLauncher<Intent>,
        googleSignInClient: GoogleSignInClient,
    ) {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent

            signInLauncher.launch(signInIntent)
        }
    }

    suspend fun firebaseAuthWithGoogle(
        idToken: String,
    ): SignInResult {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()

        if (authResult.additionalUserInfo?.isNewUser == true) {
            userRepository.createUser(authResult.user!!)
        }

        val user = authResult.user?.toUser()

        return SignInResult(user, null)
    }

    suspend fun signInWithGitHub(
        context: Context
    ): SignInResult {
        val provider = OAuthProvider.newBuilder("github.com")

        provider.addCustomParameter("login", "")
        provider.scopes = listOf("user:email")

        val pendingResultTask = auth.pendingAuthResult?.await()

        if (pendingResultTask != null) {
            if (pendingResultTask.additionalUserInfo?.isNewUser == true) {
                userRepository.createUser(pendingResultTask.user!!)
            }

            return SignInResult(pendingResultTask.user?.toUser(), null)
        }

        val result =
            auth.startActivityForSignInWithProvider(context as Activity, provider.build()).await()

        if (result.user != null) {
            if (result.additionalUserInfo?.isNewUser == true) {
                userRepository.createUser(result.user!!)
            }
        }

        return SignInResult(result.user?.toUser(), null)
    }

    suspend fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()

            if (e is CancellationException) {
                throw e
            }
        }
    }
}