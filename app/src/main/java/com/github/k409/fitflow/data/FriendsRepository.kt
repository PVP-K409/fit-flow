package com.github.k409.fitflow.data

import com.github.k409.fitflow.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

    fun getFriendRequests(): Flow<List<User>> =
        getFriendDocumentReference(auth.currentUser!!.uid)
            .whereEqualTo("accepted", false)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    document.toObject<User>()
                }
            }
            .catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }

    fun getFriends(): Flow<List<User>> =
        getFriendDocumentReference(auth.currentUser!!.uid)
            .whereEqualTo("accepted", true)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    document.toObject<User>()
                }
            }
            .catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }

//    private fun getFriends(): Flow<List<String>> =
//        getFriendDocumentReference(auth.currentUser!!.uid)
//            .snapshots()
//            .map { snapshot ->
//                snapshot.documents.filter { document ->
//                    document.getBoolean("accepted") == true
//                } .map { document ->
//                    document.id}
//            }.catch { e ->
//                e.printStackTrace()
//                emit(emptyList())
//            }
//
//    private fun getFriendRequests(): Flow<List<String>> =
//        getFriendDocumentReference(auth.currentUser!!.uid)
//            .snapshots()
//            .map { snapshot ->
//                snapshot.documents.filter { document ->
//                    document.getBoolean("accepted") == false
//                } .map { document ->
//                    document.id}
//            }.catch { e ->
//                e.printStackTrace()
//                emit(emptyList())
//            }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    fun getFriendsDetails(): Flow<List<User>> =
//        getFriends()
//            .flatMapConcat { friends ->
//                if (friends.isEmpty()) {
//                    flowOf(emptyList())
//                } else {
//                    db.collection(USERS_COLLECTION)
//                        .whereIn("uid", friends)
//                        .snapshots()
//                        .map { snapshot ->
//                            snapshot.documents.mapNotNull { it.toObject<User>() }
//                        }
//                }
//            }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    fun getFriendRequestsDetails(): Flow<List<User>> =
//        getFriendRequests()
//            .flatMapConcat { requests ->
//                if (requests.isEmpty()) {
//                    flowOf(emptyList())
//                } else {
//                    db.collection(USERS_COLLECTION)
//                        .whereIn("uid", requests)
//                        .snapshots()
//                        .map { snapshot ->
//                            snapshot.documents.mapNotNull { it.toObject<User>() }
//                        }
//                }
//            }

    suspend fun sendFriendRequest(email: String) {

        val currentUser = auth.currentUser!!.uid
        val user = db.collection(USERS_COLLECTION)
            .document(currentUser)
            .get().await().toObject<User>() ?: User()

        userRepository.searchUserByEmail(email).collect { uid ->
            uid?.let {
                try {
                        getFriendDocumentReference(uid)
                            .document(currentUser)
                            .set(mapOf(
                                "accepted" to false,
                                "name" to user.name,
                                "email" to user.email,
                                "photoUrl" to user.photoUrl,
                                "uid" to user.uid,
                                "xp" to user.xp
                            )).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun acceptFriendRequest(uid: String) {

        val currentUser = auth.currentUser!!.uid
        val user = db.collection(USERS_COLLECTION)
            .document(currentUser)
            .get().await().toObject<User>() ?: User()

        try {
            getFriendDocumentReference(currentUser)
                .document(uid)
                .update("accepted", true).await()

            getFriendDocumentReference(uid)
                .document(currentUser)
                .set(mapOf(
                    "accepted" to true,
                    "name" to user.name,
                    "email" to user.email,
                    "photoUrl" to user.photoUrl,
                    "uid" to user.uid,
                    "xp" to user.xp
                )).await()
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