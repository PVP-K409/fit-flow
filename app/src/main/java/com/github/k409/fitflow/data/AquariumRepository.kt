package com.github.k409.fitflow.data

import com.github.k409.fitflow.model.AquariumStats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val AQUARIUM_COLLECTION = "aquarium"
private const val TAG = "AquariumRepository"

const val WATER_LEVEL_CHANGE_DAILY = 0.25f
const val HEALTH_LEVEL_CHANGE_DAILY = 0.25f
const val HEALTH_LEVEL_CHANGE_WEEKLY = 0.5f

class AquariumRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {

    fun getAquariumStats(): Flow<AquariumStats> {
        val uid = auth.currentUser!!.uid

        return getDocumentReference(uid)
            .snapshots()
            .map {
                it.toObject<AquariumStats>() ?: AquariumStats()
            }
    }

    fun createAquariumStats() {
        val uid = auth.currentUser?.uid ?: return

        val ref = getDocumentReference(uid)
        val stats = AquariumStats()

        ref.set(stats)
    }

    fun changeWaterLevel(changeValue: Float) {
        val uid = auth.currentUser?.uid ?: return

        val ref = getDocumentReference(uid)
        val fieldName = AquariumStats::waterLevel.name

        ref.get()
            .addOnSuccessListener { snapshot ->
                val newWaterLevel = snapshot.getDouble(fieldName)!! + changeValue
                ref.update(fieldName, newWaterLevel.coerceIn(0.0, 1.0))
            }
    }

    fun changeHealthLevel(changeValue: Float) {
        val uid = auth.currentUser?.uid ?: return

        val ref = getDocumentReference(uid)
        val fieldName = AquariumStats::healthLevel.name

        ref.get()
            .addOnSuccessListener { snapshot ->
                val newHealthLevel = snapshot.getDouble(fieldName)!! + changeValue
                ref.update(fieldName, newHealthLevel.coerceIn(0.0, 1.0))
            }
    }

    private fun getDocumentReference(
        uid: String,
    ): DocumentReference {
        return db.collection(AQUARIUM_COLLECTION)
            .document(uid)
    }
}
