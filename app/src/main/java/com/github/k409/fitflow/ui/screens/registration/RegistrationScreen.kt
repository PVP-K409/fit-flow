package com.github.k409.fitflow.ui.screens.registration

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider

@Composable
fun RegistrationScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordsMatch by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(false) }
    val firebaseAuth = FirebaseAuth.getInstance()

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

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = {
                email = it
                isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()},
            label = { Text("Email")}
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password")},
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                passwordsMatch = it == password
            },
            label = { Text("Confirm password")},
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        if (!passwordsMatch) {
            Text(
                text = "Passwords do not match",
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {if (isEmailValid && passwordsMatch &&
                            email.isNotBlank() && password.isNotBlank() &&
                            confirmPassword.isNotBlank()) {
                                registerWithEmailPassword(email, password, firebaseAuth) { success ->
                                    if (success){
                                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                         },
            enabled = isEmailValid
                    && passwordsMatch
                    && email.isNotBlank()
                    && password.isNotBlank()
                    && confirmPassword.isNotBlank()) {
            Text(text = "Register")
        }

        // Call signInWithGoogle passing signInLauncher as a parameter
        Button(
            onClick = { signInWithGoogle(signInLauncher, googleSignInClient, firebaseAuth) },
        ) {
            Text(text = "Sign in with Google")
        }

        Button(
            onClick = { //signInWithGitHub(signInLauncher)
                        //setupGithubWebviewDialog(context, githubAuthURLFull)
                        signInWithGitHub(email, context, firebaseAuth)
                 },
        ) {
            Text(text = "Sign in with GitHub")
        }
    }
}

private fun signInWithGitHub(email: String, context: Context, mAuth: FirebaseAuth){
    val provider = OAuthProvider.newBuilder("github.com")
    // Target specific email with login hint.
    provider.addCustomParameter("login", email)
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

private fun registerWithEmailPassword(
    email: String,
    password: String,
    firebaseAuth: FirebaseAuth,
    callback: (Boolean) -> Unit
){
    firebaseAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener{ task ->
            if (task.isSuccessful){
                callback.invoke(true)
            } else {
                callback.invoke(false)
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