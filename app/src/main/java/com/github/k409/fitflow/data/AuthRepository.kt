package com.github.k409.fitflow.data

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.toUser
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class SignInResult(
    val user: User?,
    val errorMessage: String?,
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

    fun signOut() {
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
