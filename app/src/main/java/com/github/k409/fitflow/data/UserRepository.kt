package com.github.k409.fitflow.data

import android.util.Log
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.toUser
import com.github.k409.fitflow.ui.screen.level.levels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val USERS_COLLECTION = "users"

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val stepsRepository: StepsRepository,
) {

    private fun getAuthState() = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }

        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser: Flow<User> = getAuthState().flatMapLatest { isUserLoggedOut ->
        if (isUserLoggedOut) {
            callbackFlow {
                trySend(User())
                awaitClose()
            }
        } else {
            getUser(auth.currentUser!!.uid)
        }
    }

    private fun getUser(uid: String): Flow<User> =
        getUserDocumentReference(uid)
            .snapshots()
            .map { it.toObject<User>() ?: User() }

    fun createUser(firebaseUser: FirebaseUser) {
        val user = firebaseUser.toUser()

        if (user.name.isEmpty()) {
            user.name = user.email
        }

        getUserDocumentReference(user.uid).set(user)

        CoroutineScope(Dispatchers.IO).launch {
            stepsRepository.setInitialSteps(user.uid)
        }
    }

    suspend fun addCoinsAndXp(
        coins: Long,
        xp: Long,
    ) {
        val uid = auth.currentUser!!.uid

        val userDocRef = getUserDocumentReference(uid)
        val user = userDocRef.get().await().toObject<User>() ?: return

        try {
            // check if user leveled up
            if (levels.any { user.xp + xp >= it.maxXP && user.xp < it.maxXP }) {
                userDocRef
                    .update(
                        mapOf(
                            "points" to FieldValue.increment(coins),
                            "xp" to FieldValue.increment(xp),
                            "hasLeveledUp" to true,
                        ),
                    ).await()
            }
            else {
                userDocRef
                    .update(
                        mapOf(
                            "points" to FieldValue.increment(coins),
                            "xp" to FieldValue.increment(xp),
                        ),
                    ).await()
            }
        } catch (e: Exception) {
            Log.e("User Repository", "Error updating user coins and xp")
        }
    }

    suspend fun updateFcmToken(token: String) {
        val uid = auth.currentUser?.uid ?: return

        try {
            getUserDocumentReference(uid)
                .update("fcmToken", token)
                .await()
        } catch (e: Exception) {
            Log.e("User Repository", "Error updating user FCM token")
            Log.e("User Repository", e.toString())
        }
    }

    suspend fun updateUserField(field: String, value: Any) {
        val uid = auth.currentUser?.uid ?: return

        try {
            getUserDocumentReference(uid)
                .update(field, value)
                .await()
        } catch (e: Exception) {
            Log.e("User Repository", "Error updating user field")
            Log.e("User Repository", e.toString())
        }
    }
    fun getAllUserProfiles(): Flow<List<User>> = callbackFlow {
        val listener = db.collection(USERS_COLLECTION)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("User Repository", "Listen failed", e)
                    return@addSnapshotListener
                }

                val users = snapshot?.documents?.mapNotNull { it.toObject<User>() } ?: emptyList()
                trySend(users)
            }

        awaitClose {
            listener.remove()
        }
    }

    private fun getUserDocumentReference(uid: String) =
        db.collection(USERS_COLLECTION)
            .document(uid)
}
