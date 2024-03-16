package com.github.k409.fitflow.data

import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.model.toUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

    suspend fun getUser(): User? {
        val currentUser = auth.currentUser ?: return null

        return try {
            getUserDocumentReference(currentUser.uid)
                .get()
                .await()
                .toObject<User>()
        } catch (e: Exception) {
            null
        }
    }

    private fun getUserDocumentReference(uid: String) =
        db.collection(USERS_COLLECTION)
            .document(uid)
}
