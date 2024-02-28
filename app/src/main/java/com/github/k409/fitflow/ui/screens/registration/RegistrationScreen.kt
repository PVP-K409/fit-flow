package com.github.k409.fitflow.ui.screens.registration

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.auth

@Composable
fun RegistrationScreen() {
    val firebaseAuth = FirebaseAuth.getInstance()
//    var userSignedIn by remember { mutableStateOf(false) }
//
//    val user = Firebase.auth.currentUser
//    if (user != null) {
//        userSignedIn = true
//    }

    val context = LocalContext.current

    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts
        .StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try{
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(context, account.idToken!!)
            } catch (e: Exception) {
                Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("917626448761-u8nvgrds5phb5pnrpr2sk0u47tbuo71p.apps.googleusercontent.com")
        .requestEmail()
        .requestId()
        .build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(LocalContext.current, gso)

    Surface(modifier = Modifier.fillMaxSize()) {}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = "Registration and sign in")

        // Call signInWithGoogle passing signInLauncher as a parameter
        Button(
            onClick = { signInWithGoogle(signInLauncher, googleSignInClient, firebaseAuth) },
        ) {
            Text(text = "Sign in with Google")
        }

        Button(
            onClick = { signInWithGitHub(context, firebaseAuth) },
        ) {
            Text(text = "Sign in with GitHub")
        }

//        Button(
//            onClick = { Firebase.auth.signOut()},
//            enabled = userSignedIn
//        ) {
//            Text(text = "Sign out")
//        }
    }
}

private fun signInWithGitHub(context: Context, mAuth: FirebaseAuth){
    val provider = OAuthProvider.newBuilder("github.com")
    // Target specific email with login hint.
    provider.addCustomParameter("login", "")
    // Request read access to a user's email addresses.
    // This must be preconfigured in the app's API permissions.
    provider.scopes = listOf("user:email")
    val pendingResultTask = mAuth.pendingAuthResult
    if (pendingResultTask != null) {
        // There's something already here! Finish the sign-in for your user.
        pendingResultTask
            .addOnSuccessListener {
                // User is signed in.
                // IdP data available in
                // authResult.getAdditionalUserInfo().getProfile().
                // The OAuth access token can also be retrieved:
                // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                // The OAuth secret can be retrieved by calling:
                // ((OAuthCredential)authResult.getCredential()).getSecret().
                Toast.makeText(context, "Github sign in successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Handle failure.
                Toast.makeText(context, "Github sign in failed", Toast.LENGTH_SHORT).show()
            }
    } else {
        mAuth
            .startActivityForSignInWithProvider(context as Activity, provider.build())
            .addOnSuccessListener {
                // User is signed in.
                // IdP data available in
                // authResult.getAdditionalUserInfo().getProfile().
                // The OAuth access token can also be retrieved:
                // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                // The OAuth secret can be retrieved by calling:
                // ((OAuthCredential)authResult.getCredential()).getSecret().
                Toast.makeText(context, "Github sign in successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Handle failure.
                Toast.makeText(context, "Github sign in failed", Toast.LENGTH_SHORT).show()
            }
    }
}


private fun signInWithGoogle(signInLauncher: ActivityResultLauncher<Intent>,googleSignInClient: GoogleSignInClient, firebaseAuth: FirebaseAuth){
    googleSignInClient.signOut().addOnCompleteListener {
        // Launch the sign-in intent after sign-out
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }
}

private fun firebaseAuthWithGoogle(context: Context, idToken: String) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    FirebaseAuth.getInstance().signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(context, "Google sign in successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    RegistrationScreen()
}