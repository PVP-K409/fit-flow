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
private const val TAG = "AquariumRepository"

const val WATER_LEVEL_CHANGE_DAILY = 0.1f
const val HEALTH_LEVEL_CHANGE_DAILY = 0.05f
const val HEALTH_LEVEL_CHANGE_WEEKLY = 0.25f

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

    fun changeWaterLevel(changeValue: Float) {
        val uid = auth.currentUser?.uid ?: return

        val ref = getDocumentReference(uid)
        val fieldName = AquariumStats::waterLevel.name

        db.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val newWaterLevel = snapshot.getDouble(fieldName)!! + changeValue

            transaction.update(ref, fieldName, newWaterLevel.coerceIn(0.0, 1.0))
        }
    }

    fun changeHealthLevel(changeValue: Float) {
        val uid = auth.currentUser?.uid ?: return

        val ref = getDocumentReference(uid)
        val fieldName = AquariumStats::healthLevel.name

        db.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val newHealthLevel = snapshot.getDouble(fieldName)!! + changeValue

            transaction.update(ref, fieldName, newHealthLevel.coerceIn(0.0, 1.0))
        }
    }

    private fun getDocumentReference(
        uid: String,
    ): DocumentReference {
        return db.collection(AQUARIUM_COLLECTION)
            .document(uid)
    }
}
