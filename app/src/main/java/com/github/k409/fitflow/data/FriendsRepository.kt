package com.github.k409.fitflow.data

import com.github.k409.fitflow.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val FRIENDS_COLLECTION = "friends"
private const val PENDING_REQUEST_FIELD = "pendingRequests"
private const val FRIENDS_LIST_FIELD = "friends"

class FriendsRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) {

    fun getFriendRequests(): Flow<List<Flow<User>>> =
        getFriendDocumentReference(auth.currentUser!!.uid)
            .snapshots()
            .map { snapshot ->
                snapshot.data?.get(PENDING_REQUEST_FIELD) as List<*>
            }
            .map { requests ->
                requests.mapNotNull { userRepository.getUser(it.toString()) }
            }
            .catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }

    fun getFriends(): Flow<List<Flow<User>>> =
        getFriendDocumentReference(auth.currentUser!!.uid)
            .snapshots()
            .map { snapshot ->
                snapshot.data?.get(FRIENDS_LIST_FIELD)as List<*>
            }
            .map { friends ->
                friends.mapNotNull { userRepository.getUser(it.toString()) }
            }
            .catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }

    suspend fun sendFriendRequest(uid: String) {
        val currentUser = auth.currentUser!!.uid

        try {
            getFriendDocumentReference(uid)
                .set(
                    mapOf(PENDING_REQUEST_FIELD to FieldValue.arrayUnion(currentUser)),
                    SetOptions.merge(),
                )
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun acceptFriendRequest(uid: String) {
        val currentUser = auth.currentUser!!.uid

        try {
            getFriendDocumentReference(currentUser)
                .set(
                    mapOf(
                        FRIENDS_LIST_FIELD to FieldValue.arrayUnion(uid),
                        PENDING_REQUEST_FIELD to FieldValue.arrayRemove(uid),
                    ),
                    SetOptions.merge(),
                )
                .await()

            getFriendDocumentReference(uid)
                .set(
                    mapOf(
                        FRIENDS_LIST_FIELD to FieldValue.arrayUnion(currentUser),
                        PENDING_REQUEST_FIELD to FieldValue.arrayRemove(currentUser),
                    ),
                    SetOptions.merge(),
                )
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun declineFriendRequest(uid: String) {
        try {
            getFriendDocumentReference(auth.currentUser!!.uid)
                .update(PENDING_REQUEST_FIELD, FieldValue.arrayRemove(uid))
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun removeFriend(uid: String) {
        try {
            getFriendDocumentReference(auth.currentUser!!.uid)
                .update(FRIENDS_LIST_FIELD, FieldValue.arrayRemove(uid))
                .await()

            getFriendDocumentReference(uid)
                .update(FRIENDS_LIST_FIELD, FieldValue.arrayRemove(auth.currentUser!!.uid))
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFriendDocumentReference(uid: String) =
        db.collection(FRIENDS_COLLECTION)
            .document(uid)
}
