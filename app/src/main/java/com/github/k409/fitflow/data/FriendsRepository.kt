package com.github.k409.fitflow.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


private const val FRIENDS_COLLECTION = "friends"

class FriendsRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    //var success: Boolean = true,
) {

    fun getFriendsUID(): Flow<List<String>> =
        getUserDocumentReference(auth.currentUser!!.uid)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.filter { document ->
                    document.getBoolean("accepted") == true
                } .map { document ->
                    document.id}
            }

    suspend fun sendFriendRequest(email: String) {

        userRepository.searchUserByEmail(email).collect { uid ->
            uid?.let {
                try {
                    getUserDocumentReference(uid)
                        .document(auth.currentUser!!.uid)
                        .set(mapOf("accepted" to false)).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                    //success = false
                }
            }
        }

        //return success
    }

    suspend fun acceptFriendRequest(uid: String) {
        try {
            getUserDocumentReference(auth.currentUser!!.uid)
                .document(uid)
                .update("accepted", true).await()
        } catch (e: Exception) {
            e.printStackTrace()
            //success = false
        }

        //return success
    }

    suspend fun deleteFriendRequest(uid: String) {
        try {
            getUserDocumentReference(auth.currentUser!!.uid)
                .document(uid)
                .delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
            //success = false
        }

        //return success
    }

    private fun getUserDocumentReference(uid: String) =
        db.collection(FRIENDS_COLLECTION)
            .document("Status")
            .collection(uid)
}