package com.github.k409.fitflow.ui.screens.registration

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider

fun signInWithGoogle(
    signInLauncher: ActivityResultLauncher<Intent>,
    googleSignInClient: GoogleSignInClient,
) {
    googleSignInClient.signOut().addOnCompleteListener {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }
}

fun firebaseAuthWithGoogle(
    context: Context,
    idToken: String,
) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Toast.makeText(context, "Google sign in successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show()
        }
    }
}

fun signInWithGitHub(
    context: Context,
    mAuth: FirebaseAuth,
) {
    val provider = OAuthProvider.newBuilder("github.com")

    provider.addCustomParameter("login", "")
    provider.scopes = listOf("user:email")

    val pendingResultTask = mAuth.pendingAuthResult

    if (pendingResultTask != null) {
        pendingResultTask.addOnSuccessListener {
            Toast.makeText(context, "Github sign in successful", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Github sign in failed", Toast.LENGTH_SHORT).show()
        }
    } else {
        mAuth.startActivityForSignInWithProvider(context as Activity, provider.build())
            .addOnSuccessListener {
                Toast.makeText(context, "Github sign in successful", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Github sign in failed", Toast.LENGTH_SHORT).show()
            }
    }
}
