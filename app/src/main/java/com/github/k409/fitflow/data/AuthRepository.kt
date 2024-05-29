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
import com.github.k409.fitflow.service.SnackbarManager
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "AuthRepository"

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository,
    private val aquariumRepository: AquariumRepository,
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
                context.getString(R.string.no_credentials_found_please_add_a_google_account_to_your_device),
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

            aquariumRepository.createAquariumStats()

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

    private suspend fun deleteUserAndData(activityContext: Context) {
        try{
            val result = credentialManager.getCredential(activityContext, getCredentialRequest)
            val credential = result.credential
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val credentiall = GoogleAuthProvider.getCredential(idToken, null)
                auth.currentUser?.reauthenticate(credentiall)?.await()
            }

            val userId = auth.currentUser?.uid ?: return
            val user = auth.currentUser ?: return
            try {
                val collections = listOf("aquarium", "friends", "users")

                collections.forEach { collection ->
                    val docRef = db.collection(collection).document(userId)
                    docRef.delete().await()
                }

                val inventoryDocs = db.collection("inventory")
                    .document(userId)
                    .collection("items").get().await()

                for (doc in inventoryDocs.documents) {
                    try {
                        doc.reference.delete().await()
                    } catch (e: Exception) {
                        Log.e("Auth Repository", "Error deleting inventory item")
                    }
                }

                val goalsDailyDocs = db.collection("goals")
                    .document(userId)
                    .collection("Daily").get().await()

                for (doc in goalsDailyDocs.documents) {
                    try {
                        doc.reference.delete().await()
                    } catch (e: Exception) {
                        Log.e("Auth Repository", "Error deleting daily goals")
                    }
                }

                val goalsWeeklyDocs = db.collection("goals")
                    .document(userId)
                    .collection("Weekly").get().await()

                for (doc in goalsWeeklyDocs.documents) {
                    try {
                        doc.reference.delete().await()
                    } catch (e: Exception) {
                        Log.e("Auth Repository", "Error deleting weekly goals")
                    }
                }

                val journalHydrationDocs = db.collection("journal")
                    .document(userId)
                    .collection("hydration").get().await()

                for (doc in journalHydrationDocs.documents) {
                    try {
                        doc.reference.delete().await()
                    } catch (e: Exception) {
                        Log.e("Auth Repository", "Error deleting hydration journal")
                    }
                }

                val journalStepsDocs = db.collection("journal")
                    .document(userId)
                    .collection("steps").get().await()

                for (doc in journalStepsDocs.documents) {
                    try {
                        doc.reference.delete().await()
                    } catch (e: Exception) {
                        Log.e("Auth Repository", "Error deleting steps journal")
                    }
                }

                val friendsCollection = db.collection("friends")
                val friendsDocs = friendsCollection.get().await()
                for (doc in friendsDocs.documents) {
                    val docRef = doc.reference
                    val data = doc.data ?: continue

                    val friendsList = data["friends"] as? MutableList<*> ?: continue
                    val pendingRequestsList = data["pendingRequests"] as? MutableList<*> ?: continue

                    if (friendsList.contains(userId)) {
                        friendsList.remove(userId)
                        docRef.update("friends", friendsList).await()
                    }

                    if (pendingRequestsList.contains(userId)) {
                        pendingRequestsList.remove(userId)
                        docRef.update("pendingRequests", pendingRequestsList).await()
                    }
                }
            } catch (e: Exception) {
                Log.e("Auth Repository", "Error deleting user data")
                Log.e("Auth Repository", e.toString())
            }
            try {
                user.delete().await()
            } catch (e: Exception) {
                Log.e("Auth Repository", "Error deleting user")
                Log.e("Auth Repository", e.toString())
            }

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("Auth Repository", "Error reauthenticating user: Invalid credentials")
            Log.e("Auth Repository", e.toString())
            SnackbarManager.showMessage(
                activityContext.getString(R.string.invalid_credentials))
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Log.e("Auth Repository", "Error reauthenticating user: Recent login required")
            Log.e("Auth Repository", e.toString())
            SnackbarManager.showMessage(
                activityContext.getString(R.string.recent_login_required_please_sign_in_again))
        } catch (e: Exception) {
            Log.e("Auth Repository", "Error reauthenticating user")
            Log.e("Auth Repository", e.toString())
        }
    }

    fun logoutAndDeleteUSer(context: Context) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            deleteUserAndData(context)
        }
    }
}

data class SignInResult(
    val user: User?,
    val errorMessage: String?,
)
