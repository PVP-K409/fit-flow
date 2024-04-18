package com.github.k409.fitflow.ui.screen.hydration

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.R
import com.github.k409.fitflow.data.HydrationRepository
import com.github.k409.fitflow.model.HydrationRecord
import com.github.k409.fitflow.model.Notification
import com.github.k409.fitflow.model.NotificationChannel
import com.github.k409.fitflow.service.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class HydrationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hydrationRepository: HydrationRepository,
    private val notificationService: NotificationService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HydrationUiState())
    val uiState = _uiState.asStateFlow()

    val hydrationLogsUiState: StateFlow<HydrationLogsUiState> = combine(
        hydrationRepository.getHydrationRecordsGroupedByWeek(),
        hydrationRepository.getWaterIntakeGoal(),
    ) { records, goal ->
        HydrationLogsUiState.Success(records, goal)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HydrationLogsUiState.Loading,
    )

    init {
        getTodayWaterIntake()
        getCupSize()
        getWaterIntakeGoal()
        getHistory()
    }

    fun addWaterCup() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hydrationRepository.addWaterIntake(_uiState.value.cupSize)
            }
        }
    }

    fun setCupSize(size: Int) {
        viewModelScope.launch {
            hydrationRepository.setCupSize(size)

            _uiState.update {
                it.copy(cupSize = size)
            }
        }
    }

    private fun getTodayWaterIntake() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hydrationRepository.getTodayWaterIntake()
                    .collect { record ->
                        _uiState.update {
                            it.copy(today = record.waterIntake)
                        }
                    }
            }
        }
    }

    private fun getWaterIntakeGoal() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hydrationRepository
                    .getWaterIntakeGoal()
                    .collect { goal ->
                        _uiState.update {
                            it.copy(dailyGoal = goal)
                        }
                    }
            }
        }
    }

    private fun getHistory() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hydrationRepository.getThisMonthStats().collect { stats ->
                    _uiState.update {
                        it.copy(
                            stats = stats,
                        )
                    }
                }
            }
        }
    }

    private fun getCupSize() {
        viewModelScope.launch {
            hydrationRepository.getCupSize().collect { cupSize ->
                _uiState.update {
                    it.copy(cupSize = cupSize)
                }
            }
        }
    }

    fun scheduleWaterReminder() {
        notificationService.post(
            notification = Notification(
                channel = NotificationChannel.HydrationReminder,
                title = context.getString(R.string.hydration_notification_title),
                text = context.getString(R.string.hydration_notification_text),
            ),
            delay = Duration.ofSeconds(10),
        )
    }
}

sealed interface HydrationLogsUiState {
    data object Loading : HydrationLogsUiState

    data class Success(
        val groupedRecords: Map<String, List<HydrationRecord>> = emptyMap(),
        val goal: Int = 0,
    ) : HydrationLogsUiState
}
