package com.github.k409.fitflow.data

import android.util.Log
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.levels
import com.github.k409.fitflow.model.toUser
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

    fun getUser(uid: String): Flow<User> =
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
            } else {
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

    fun getAllUserProfiles(): Flow<List<User>> =
        db.collection(USERS_COLLECTION)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject<User>() }
            }

    suspend fun getUserWeight(): Double {
        val uid = auth.currentUser?.uid ?: return 0.0

        return getUserDocumentReference(uid)
            .get()
            .await()
            .toObject<User>()
            ?.weight ?: 0.0
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

    suspend fun searchUserByEmail(email: String): User {
        return try {
            db.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.toObject<User>()
                ?: User()
        } catch (e: Exception) {
            Log.e("User Repository", "Error searching user by email")
            Log.e("User Repository", e.toString())
            User()
        }
    }

    suspend fun searchUsersByEmail(email: String): List<User> {
        return try {
            db.collection(USERS_COLLECTION)
                .whereGreaterThanOrEqualTo("email", email)
                .whereLessThanOrEqualTo("email", email + "\uf8ff")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject<User>() }
        } catch (e: Exception) {
            Log.e("User Repository", "Error searching users by email")
            Log.e("User Repository", e.toString())
            emptyList()
        }
    }

    suspend fun searchUserByName(name: String): User {
        return try {
            db.collection(USERS_COLLECTION)
                .whereEqualTo("name", name)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.toObject<User>() ?: User()
        } catch (e: Exception) {
            Log.e("User Repository", "Error searching user by name")
            Log.e("User Repository", e.toString())
            User()
        }
    }

    suspend fun searchUsersByName(name: String): List<User> {
        return try {
            db.collection(USERS_COLLECTION)
                .whereGreaterThanOrEqualTo("name", name)
                .whereLessThanOrEqualTo("name", name + "\uf8ff")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject<User>() }
        } catch (e: Exception) {
            Log.e("User Repository", "Error searching users by name")
            Log.e("User Repository", e.toString())
            emptyList()
        }
    }

    private fun getUserDocumentReference(uid: String) =
        db.collection(USERS_COLLECTION)
            .document(uid)
}
