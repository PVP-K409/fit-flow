package com.github.k409.fitflow.data

import com.github.k409.fitflow.data.preferences.PreferenceKeys
import com.github.k409.fitflow.data.preferences.PreferencesRepository
import com.github.k409.fitflow.model.DrinkReminderState
import com.github.k409.fitflow.model.HydrationRecord
import com.github.k409.fitflow.model.HydrationStats
import com.github.k409.fitflow.service.HydrationNotificationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

private const val JOURNAL_COLLECTION = "journal"
private const val HYDRATION_COLLECTION = "hydration"

private const val WATER_INTAKE_FIELD = "waterIntake"
private const val DATE_FIELD = "date"

class HydrationRepository @Inject constructor(
    private val userRepository: UserRepository,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val preferencesRepository: PreferencesRepository,
    private val hydrationNotificationService: HydrationNotificationService,
    private val aquariumRepository: AquariumRepository,
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

        scheduleHydrationNotifications()

        val goal = getWaterIntakeGoal().first()

        val updatedWaterIntake = getTodayWaterIntake().first().waterIntake
        val previousWaterIntake = updatedWaterIntake - waterIntake

        if (goal in (previousWaterIntake + 1)..updatedWaterIntake) {
            aquariumRepository.changeWaterLevel(WATER_LEVEL_CHANGE_DAILY)
        }
    }

    fun getWaterIntakeGoal(): Flow<Int> {
        return userRepository.currentUser.map {
            calculateWaterIntakeGoal(it.weight)
        }
    }

    suspend fun updateWaterIntakeGoal() {
        scheduleHydrationNotifications()
    }

    private fun calculateWaterIntakeGoal(weight: Double): Int {
        return if (weight == 0.0) {
            2000
        } else {
            (weight * 30).toInt()
        }
    }

    fun getTodayWaterIntake(): Flow<HydrationRecord> {
        val uid = auth.currentUser?.uid ?: return flowOf(HydrationRecord())
        val todayDate = LocalDate.now().toString()

        return getWaterIntake(todayDate)
    }

    fun getWaterIntake(date: String): Flow<HydrationRecord> {
        val uid = auth.currentUser?.uid ?: return flowOf(HydrationRecord())

        return db.collection(JOURNAL_COLLECTION)
            .document(uid)
            .collection(HYDRATION_COLLECTION)
            .document(date)
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

    fun getHydrationRecordsGroupedByMonth(): Flow<Map<String, List<HydrationRecord>>> {
        return getHydrationRecords()
            .map { records ->
                records.groupBy { record ->
                    record.date.substring(0, 7)
                }
            }
    }

    fun getHydrationRecordsGroupedByWeek(): Flow<Map<String, List<HydrationRecord>>> {
        return getHydrationRecords()
            .map { records ->
                records.groupBy { record ->
                    val date = LocalDate.parse(record.date)

                    val startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    val endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

                    "$startOfWeek - $endOfWeek"
                }
            }
    }

    private fun getHydrationRecords(orderDirection: Query.Direction = Query.Direction.DESCENDING): Flow<List<HydrationRecord>> {
        val uid = auth.currentUser!!.uid

        return db.collection(JOURNAL_COLLECTION)
            .document(uid)
            .collection(HYDRATION_COLLECTION)
            .orderBy(
                DATE_FIELD,
                orderDirection,
            )
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { document ->
                    document.toObject<HydrationRecord>() ?: HydrationRecord()
                }
            }
    }

    suspend fun setCupSize(size: Int) {
        preferencesRepository.putPreference(PreferenceKeys.CUP_SIZE, size)
        scheduleHydrationNotifications()
    }

    fun getCupSize(): Flow<Int> {
        return preferencesRepository.getPreference(PreferenceKeys.CUP_SIZE, 250)
    }

    private fun getDrinkReminderState(): Flow<DrinkReminderState> {
        return combine(
            getWaterIntakeGoal(),
            getCupSize(),
            getTodayWaterIntake(),
        ) { goal, cupSize, record ->
            DrinkReminderState(
                cupSize = cupSize,
                intakeGoal = goal,
                todayWaterIntake = record.waterIntake,
            )
        }
    }

    suspend fun scheduleHydrationNotifications() {
        auth.currentUser?.uid ?: return

        val state = getDrinkReminderState().first()

        hydrationNotificationService.scheduleNotifications(state)
    }
}
