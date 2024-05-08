package com.github.k409.fitflow.data

import com.github.k409.fitflow.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


private const val FRIENDS_COLLECTION = "friends"
private const val USERS_COLLECTION = "users"

class FriendsRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) {

    private fun getFriends(): Flow<List<String>> =
        getFriendDocumentReference(auth.currentUser!!.uid)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.filter { document ->
                    document.getBoolean("accepted") == true
                } .map { document ->
                    document.id}
            }.catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }

    private fun getFriendRequests(): Flow<List<String>> =
        getFriendDocumentReference(auth.currentUser!!.uid)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.filter { document ->
                    document.getBoolean("accepted") == false
                } .map { document ->
                    document.id}
            }.catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFriendsDetails(): Flow<List<User>> =
        getFriends()
            .flatMapConcat { friends ->
                if (friends.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    db.collection(USERS_COLLECTION)
                        .whereIn("uid", friends)
                        .snapshots()
                        .map { snapshot ->
                            snapshot.documents.mapNotNull { it.toObject<User>() }
                        }
                }
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFriendRequestsDetails(): Flow<List<User>> =
        getFriendRequests()
            .flatMapConcat { requests ->
                if (requests.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    db.collection(USERS_COLLECTION)
                        .whereIn("uid", requests)
                        .snapshots()
                        .map { snapshot ->
                            snapshot.documents.mapNotNull { it.toObject<User>() }
                        }
                }
            }

    suspend fun sendFriendRequest(email: String) {
        userRepository.searchUserByEmail(email).collect { uid ->
            uid?.let {
                try {
                        getFriendDocumentReference(uid)
                            .document(auth.currentUser!!.uid)
                            .set(mapOf("accepted" to false)).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun acceptFriendRequest(uid: String) {
        try {
            getFriendDocumentReference(auth.currentUser!!.uid)
                .document(uid)
                .update("accepted", true).await()

            getFriendDocumentReference(uid)
                .document(auth.currentUser!!.uid)
                .set(mapOf("accepted" to true)).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun declineFriendRequest(uid: String) {
        try {
            getFriendDocumentReference(auth.currentUser!!.uid)
                .document(uid)
                .delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun removeFriend(uid: String) {
        try {
            getFriendDocumentReference(auth.currentUser!!.uid)
                .document(uid)
                .delete().await()

            getFriendDocumentReference(uid)
                .document(auth.currentUser!!.uid)
                .delete().await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFriendDocumentReference(uid: String) =
        db.collection(FRIENDS_COLLECTION)
            .document("Status")
            .collection(uid)
}