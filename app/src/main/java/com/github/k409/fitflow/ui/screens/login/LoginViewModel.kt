package com.github.k409.fitflow.ui.screens.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.github.k409.fitflow.model.toUser
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore
) : ViewModel() {
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
        context: Context,
        idToken: String,
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()

        if (authResult.user != null) {
            Toast.makeText(context, "Google sign in successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show()
        }

        if (authResult.additionalUserInfo?.isNewUser == true) {
            createUser(authResult.user!!)
        }
    }

    suspend fun signInWithGitHub(
        context: Context
    ) {
        val provider = OAuthProvider.newBuilder("github.com")

        provider.addCustomParameter("login", "")
        provider.scopes = listOf("user:email")

        val pendingResultTask = auth.pendingAuthResult?.await()

        if (pendingResultTask != null) {
            if (pendingResultTask.user != null) {
                Toast.makeText(context, "Github sign in successful", Toast.LENGTH_SHORT).show()

                if (pendingResultTask.additionalUserInfo?.isNewUser == true) {
                    createUser(pendingResultTask.user!!)
                }

            } else {
                Toast.makeText(context, "Github sign in failed", Toast.LENGTH_SHORT).show()
            }

            return
        }

        val result =
            auth.startActivityForSignInWithProvider(context as Activity, provider.build()).await()

        if (result.user != null) {
            Toast.makeText(context, "Github sign in successful", Toast.LENGTH_SHORT).show()

            if (result.additionalUserInfo?.isNewUser == true) {
                createUser(result.user!!)
            }
        } else {
            Toast.makeText(context, "Github sign in failed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun createUser(firebaseUser: FirebaseUser) {
        val user = firebaseUser.toUser()

        db.collection("users").document(user.uid).set(user)
    }
}
