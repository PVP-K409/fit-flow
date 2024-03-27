package com.github.k409.fitflow.data

import android.util.Log
import com.github.k409.fitflow.model.GoalRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val GOALS_COLLECTION = "goals"
private const val WEEKLY_COLLECTION = "Weekly"
private const val DAILY_COLLECTION = "Daily"

class GoalsRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    suspend fun setGoals(
        goals: MutableMap<String, GoalRecord>,
        period: String = DAILY_COLLECTION,
    ) {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        val todayDateString = LocalDate.now().toString()

        val goalsDocumentRef = getGoalsDocumentReference(uid, period, todayDateString)

        goalsDocumentRef
            .set(goals)
            .await()
    }
    suspend fun getDailyGoals(
        date: String,
    ): MutableMap<String, GoalRecord>? {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        val goalsDocumentSnapshot = getGoalsDocumentReference(uid, DAILY_COLLECTION, date)
            .get()
            .await()

        if (!goalsDocumentSnapshot.exists()) {
            return null
        }

        val goalsData = goalsDocumentSnapshot.data ?: return null

        val goalsMap = mutableMapOf<String, GoalRecord>()

        goalsData.forEach { (key, value) ->
            if (value is Map<*, *> && value.keys.all { it is String }) {
                @Suppress("UNCHECKED_CAST")
                val goalMap = value as Map<String, Any>

                val goalRecord = goalMap.toGoalRecord()
                goalsMap[key] = goalRecord
            }
        }

        return goalsMap
    }

    suspend fun getWeeklyGoals(
        date: String,
    ): MutableMap<String, GoalRecord>? {

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val endDate = LocalDate.parse(date, dateFormatter)
        val dayCount: Long = 6

        val currentUser = auth.currentUser ?: return null
        val uid = currentUser.uid

        val goalsMap = mutableMapOf<String, GoalRecord>()

        for (i in 0..dayCount) {
            val currentDay = endDate.minusDays(i)
            val dateString = currentDay.format(dateFormatter)
            val documentSnapshot = db.collection(GOALS_COLLECTION)
                .document(uid)
                .collection(WEEKLY_COLLECTION)
                .document(dateString)
                .get()
                .await()

            val goalsData = documentSnapshot.data ?: continue

            goalsData.forEach { (key, value) ->
                if (value is Map<*, *> && value.keys.all { it is String }) {
                    @Suppress("UNCHECKED_CAST")
                    val goalMap = value as Map<String, Any>

                    val goalRecord = goalMap.toGoalRecord()

                    if(!goalMap.keys.contains(key) && goalRecord.endDate > endDate.toString()) goalsMap[key] = goalRecord
                }
            }
        }
        return goalsMap.ifEmpty { null }

    }

    suspend fun updateGoals(
        goals: MutableMap<String, GoalRecord>,
        date: String,
        period: String = DAILY_COLLECTION,
    ) {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        val filteredGoals = goals.filterValues { goalRecord ->
            val goalEndDate = LocalDate.parse(goalRecord.endDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val currentDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
            goalEndDate.isAfter(currentDate)
        }

        if (filteredGoals.isNotEmpty()) {
            try {
                val goalsDocumentReference = getGoalsDocumentReference(uid, period, date)

                goalsDocumentReference.set(
                    filteredGoals,
                    SetOptions.merge()
                ).await()
            } catch (e: FirebaseFirestoreException) {
                Log.e("Goals Repository", "Error updating goals", e)
            }
        }
    }

    private fun getGoalsDocumentReference(
        uid: String,
        period: String,
        recordDate: String,
    ): DocumentReference {
        return db.collection(GOALS_COLLECTION)
            .document(uid)
            .collection(period).
            document(recordDate)
    }

    private fun Map<String, Any>.toGoalRecord(): GoalRecord {
        return GoalRecord(
            description = this["description"] as? String ?: "",
            type = this["type"] as? String ?: "",
            target = this["target"] as? Double ?: 0.0,
            currentProgress = this["currentProgress"] as? Double ?: 0.0,
            points = this["points"] as? Long ?: 0L,
            xp = this["xp"] as? Long ?: 0L,
            startDate = this["startDate"] as? String ?: "",
            endDate = this["endDate"] as? String ?: "",
            completed = this["completed"] as? Boolean ?: false,
        )
    }
}