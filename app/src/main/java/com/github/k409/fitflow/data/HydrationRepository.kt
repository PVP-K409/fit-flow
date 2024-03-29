package com.github.k409.fitflow.data

import com.github.k409.fitflow.model.HydrationRecord
import com.github.k409.fitflow.model.HydrationStats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

private const val JOURNAL_COLLECTION = "journal"
private const val HYDRATION_COLLECTION = "hydration"

private const val WATER_INTAKE_FIELD = "waterIntake"
private const val DATE_FIELD = "date"

class HydrationRepository @Inject constructor(
    private val userRepository: UserRepository,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    suspend fun addWaterIntake(waterIntake: Int) {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        val todayDateString = LocalDate.now().toString()

        val hydrationDocumentRef =
            db.collection(JOURNAL_COLLECTION)
                .document(uid)
                .collection(HYDRATION_COLLECTION)
                .document(todayDateString)

        hydrationDocumentRef
            .set(
                mapOf(
                    WATER_INTAKE_FIELD to FieldValue.increment(waterIntake.toLong()),
                    DATE_FIELD to todayDateString,
                ),
                SetOptions.merge(),
            )
            .await()
    }

    fun getWaterIntakeGoal(): Flow<Int> {
        return userRepository.currentUser.map {
            val userWeight = it.weight

            if (userWeight == 0.0) {
                2000
            } else {
                (userWeight * 30).toInt()
            }
        }
    }

    fun getTodayWaterIntake(): Flow<HydrationRecord> {
        val uid = auth.currentUser!!.uid
        val todayDate = LocalDate.now().toString()

        return db.collection(JOURNAL_COLLECTION)
            .document(uid)
            .collection(HYDRATION_COLLECTION)
            .document(todayDate)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject<HydrationRecord>() ?: HydrationRecord()
            }
    }

    private fun getLastMonthRecords(): Flow<List<HydrationRecord>> {
        val uid = auth.currentUser!!.uid

        val periodStart = LocalDate.now().minusMonths(1)
        val periodEnd = LocalDate.now()

        return db.collection(JOURNAL_COLLECTION)
            .document(uid)
            .collection(HYDRATION_COLLECTION)
            .orderBy(
                DATE_FIELD,
                Query.Direction.ASCENDING,
            )
            .whereGreaterThanOrEqualTo(
                DATE_FIELD,
                periodStart.toString(),
            )
            .whereLessThanOrEqualTo(
                DATE_FIELD,
                periodEnd.toString(),
            )
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<HydrationRecord>() ?: HydrationRecord()
                }
            }
    }

    fun getLastMonthStats(): Flow<HydrationStats> {
        return getLastMonthRecords()
            .map {
                val yesterdayDate = LocalDate.now().minusDays(1).toString()
                val thisWeekStart = LocalDate.now().minusDays(6).toString()

                val monthIntake = it.sumOf { record -> record.waterIntake }

                val weekIntake = it.filter { record ->
                    record.date >= thisWeekStart
                }.sumOf { record -> record.waterIntake }

                val yesterday = it.find { record -> record.date == yesterdayDate }?.waterIntake ?: 0

                HydrationStats(
                    yesterdayTotalAmount = yesterday,
                    thisWeekTotalAmount = weekIntake,
                    thisMonthTotalAmount = monthIntake,
                )
            }
    }
}
