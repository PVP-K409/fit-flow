package com.github.k409.fitflow.ui.screens.registration

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    onSuccessfulSignIn: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("917626448761-u8nvgrds5phb5pnrpr2sk0u47tbuo71p.apps.googleusercontent.com")
        .requestEmail().requestId().build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(LocalContext.current, gso)

    val signInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)!!

                coroutineScope.launch {
                    registrationViewModel.firebaseAuthWithGoogle(context, account.idToken!!)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text(text = "Registration and sign in")

        Button(
            onClick = {
                registrationViewModel.signInWithGoogle(
                    signInLauncher, googleSignInClient
                )
            },
        ) {
            Text(text = "Sign in with Google")
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    registrationViewModel.signInWithGitHub(context)
                }
            },
        ) {
            Text(text = "Sign in with GitHub")
        }
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    RegistrationScreen()
}
