package com.github.k409.fitflow.data

import android.content.SharedPreferences
import android.util.Log
import com.github.k409.fitflow.features.stepcounter.StepCounter
import com.github.k409.fitflow.model.DailyStepRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject


private const val STEPS_COLLECTION = "steps"
private const val JOURNAL_COLLECTION = "journal"

class StepsRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val stepCounter: StepCounter,
    private val prefs: SharedPreferences,
) {

    suspend fun setInitialSteps(uid: String) {
        val initialSteps = stepCounter.steps()
        val today = LocalDate.now().toString()

        prefs.edit()
            .putString("lastDate", today)
            .apply()

        val initialStepRecord = DailyStepRecord(
            initialSteps = initialSteps,
            recordDate = today,
        )

        getStepsDocumentReference(uid, today)
            .set(initialStepRecord)
            .await()
    }

    suspend fun updateSteps(newSteps: DailyStepRecord) {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        try {
            val stepsDocumentRef = getStepsDocumentReference(uid, newSteps.recordDate)

            stepsDocumentRef.set(
                mapOf(
                    "totalSteps" to newSteps.totalSteps,
                    "stepsBeforeReboot" to newSteps.stepsBeforeReboot,
                    "totalDistance" to newSteps.totalDistance,
                    "caloriesBurned" to newSteps.caloriesBurned
                ),
                SetOptions.merge()
            ).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("Steps Repository", "Error updating steps", e)
        }
    }

    suspend fun getSteps(recordDate: String): DailyStepRecord? {
        val currentUser = auth.currentUser ?: return null

        val dailyStepRecordSnapshot = getStepsDocumentReference(currentUser.uid, recordDate)
            .get()
            .await()

        if (!dailyStepRecordSnapshot.exists()) {
            return null
        }

        return dailyStepRecordSnapshot.toObject<DailyStepRecord>()
    }

    private fun getStepsDocumentReference(
        uid: String,
        recordDate: String
    ): DocumentReference {
        return db.collection(JOURNAL_COLLECTION)
            .document(uid)
            .collection(STEPS_COLLECTION)
            .document(recordDate)
    }
}
