package com.github.k409.fitflow.data

import android.util.Log
import com.github.k409.fitflow.model.AquariumStats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val AQUARIUM_COLLECTION = "aquarium"

class AquariumRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {

    fun get(): Flow<AquariumStats> {
        val uid = auth.currentUser!!.uid

        return getDocumentReference(uid)
            .snapshots()
            .map {
                it.toObject<AquariumStats>() ?: AquariumStats()
            }
    }

    suspend fun set(aquariumStats: AquariumStats) {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        getDocumentReference(uid)
            .set(aquariumStats)
            .await()
    }

    suspend fun update(aquariumStats: AquariumStats) {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        try {
            getDocumentReference(uid).set(
                aquariumStats,
                SetOptions.merge(),
            ).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("Aquarium Repository", "Error updating aquarium stats", e)
        }
    }

    private fun getDocumentReference(
        uid: String,
    ): DocumentReference {
        return db.collection(AQUARIUM_COLLECTION)
            .document(uid)
    }
}
