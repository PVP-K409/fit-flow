package com.github.k409.fitflow.data

import android.content.SharedPreferences
import android.util.Log
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.service.StepCounterService
import com.github.k409.fitflow.util.getShortWeekdayNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

private const val STEPS_COLLECTION = "steps"
private const val JOURNAL_COLLECTION = "journal"

private const val DATE_FIELD = "recordDate"

class StepsRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val stepCounterService: StepCounterService,
    private val prefs: SharedPreferences,
) {

    suspend fun setInitialSteps(uid: String) {
        val initialSteps = stepCounterService.steps()
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

    suspend fun updateSteps(newStepRecord: DailyStepRecord) {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        try {
            val stepsDocumentRef = getStepsDocumentReference(uid, newStepRecord.recordDate)

            stepsDocumentRef.set(
                newStepRecord,
                SetOptions.merge(),
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

    private fun getStepRecords(
        periodStart: LocalDate,
        periodEnd: LocalDate,
    ): Flow<List<DailyStepRecord>> {
        val uid = auth.currentUser!!.uid

        return db.collection(JOURNAL_COLLECTION)
            .document(uid)
            .collection(STEPS_COLLECTION)
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
                    document.toObject<DailyStepRecord>() ?: DailyStepRecord()
                }
            }
    }
    suspend fun stepSumAndCountInPeriod(
        periodStart: LocalDate,
        periodEnd: LocalDate,
    ): Pair<Long, Int> {
        val uid = auth.currentUser!!.uid
        var totalSum: Long = 0
        var count = 0
        db.collection(JOURNAL_COLLECTION)
            .document(uid)
            .collection(STEPS_COLLECTION)
            .orderBy(DATE_FIELD, Query.Direction.ASCENDING)
            .whereGreaterThanOrEqualTo(DATE_FIELD, periodStart.toString())
            .whereLessThanOrEqualTo(DATE_FIELD, periodEnd.toString())
            .get()
            .await()
            .documents.forEach { document ->
                val stepRecord = document.toObject(DailyStepRecord::class.java) // Convert the document to DailyStepRecord
                totalSum += stepRecord?.totalSteps ?: 0
                count++
            }
        return Pair(totalSum, count)
    }

    fun getStepRecordLastWeeks(weeksCount: Int): Flow<Map<String, DailyStepRecord>> {
        val today = LocalDate.now()
        val periodStart = today
            .minusWeeks(weeksCount.toLong())
            .minusDays(today.dayOfWeek.value.toLong() - 1)

        return getStepRecords(periodStart, today)
            .map { records ->
                (0 until weeksCount).reversed().associate { i ->
                    val recordDate = today
                        .minusWeeks(i.toLong())
                        .minusDays(today.dayOfWeek.value.toLong() - 1)

                    val record = aggregateWeeklyRecord(records, recordDate)

                    recordDate.toString() to record
                }
            }
    }

    fun getStepRecordCurrentWeek(): Flow<Map<String, DailyStepRecord>> {
        val today = LocalDate.now()
        val periodStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)

        val weekdays = getShortWeekdayNames()

        return getStepRecords(periodStart, today)
            .map { records ->
                val recordsMap = mutableMapOf<String, DailyStepRecord>()

                for ((index, day) in weekdays.withIndex()) {
                    val recordDate = LocalDate.now().with(DayOfWeek.values()[index])

                    val record =
                        records.find { it.recordDate == recordDate.toString() } ?: DailyStepRecord(
                            recordDate = recordDate.toString(),
                        )

                    recordsMap[day] = record
                }

                recordsMap
            }
    }

    private fun aggregateWeeklyRecord(
        records: List<DailyStepRecord>,
        recordDate: LocalDate,
    ): DailyStepRecord {
        return records.filter {
            val recordLocalDate = LocalDate.parse(it.recordDate)

            recordLocalDate.isAfter(recordDate.minusDays(1)) && recordLocalDate.isBefore(
                recordDate.plusDays(
                    6,
                ),
            )
        }.fold(DailyStepRecord(recordDate = recordDate.toString())) { acc, dailyStepRecord ->
            acc.copy(
                totalSteps = acc.totalSteps + dailyStepRecord.totalSteps,
                caloriesBurned = acc.caloriesBurned!! + dailyStepRecord.caloriesBurned!!,
                totalDistance = acc.totalDistance!! + dailyStepRecord.totalDistance!!,
            )
        }
    }

    private fun getStepsDocumentReference(
        uid: String,
        recordDate: String,
    ): DocumentReference {
        return db.collection(JOURNAL_COLLECTION)
            .document(uid)
            .collection(STEPS_COLLECTION)
            .document(recordDate)
    }
}
