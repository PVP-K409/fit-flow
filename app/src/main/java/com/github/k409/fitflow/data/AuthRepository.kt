package com.github.k409.fitflow.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.toUser
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "AuthRepository"

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository,
    private val credentialManager: CredentialManager,
    private val getCredentialRequest: GetCredentialRequest,
    @ApplicationContext private val context: Context,
) {
    suspend fun signInWithGoogle(activityContext: Context): SignInResult {
        try {
            val result = credentialManager.getCredential(activityContext, getCredentialRequest)

            return handleSignIn(result)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException", e)

            return SignInResult(
                null,
                context.getString(R.string.no_credentials_found_please_add_a_google_account_to_your_device)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
        }

        return SignInResult(null, activityContext.getString(R.string.sign_in_failed))
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): SignInResult {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        return signInWithGoogleCredentialFirebase(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "handleSignIn:", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }

        return SignInResult(null, context.getString(R.string.sign_in_failed))
    }

    private suspend fun signInWithGoogleCredentialFirebase(
        idToken: String,
    ): SignInResult {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()

        if (authResult.additionalUserInfo?.isNewUser == true) {
            userRepository.createUser(authResult.user!!)
            // add initial fish to user's inventory
            itemRepository.addItemToUserInventory(itemRepository.getInitialFish())
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

data class SignInResult(
    val user: User?,
    val errorMessage: String?,
)
