package com.github.k409.fitflow.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.ProgressGraph
import com.github.k409.fitflow.ui.common.TextWithLabel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun ProgressGraphPage(
    activityViewModel: ActivityViewModel,
) {
    val progressUiState by activityViewModel.progressUiState.collectAsState()

    when (val uiState = progressUiState) {
        is ProgressUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }

        is ProgressUiState.Success -> {
            ProgressGraphPageContent(uiState)
        }
    }
}

@Composable
private fun ProgressGraphPageContent(uiState: ProgressUiState.Success) {
    Column(modifier = Modifier.fillMaxSize()) {
        WalkingProgressGraphContainer(
            data = uiState.currentWeek.values.toList(),
            title = stringResource(R.string.current_week_progress),
            xAxisLabels = uiState.currentWeek.keys.toList(),
            selectedValueTitle = { record ->
                LocalDate
                    .parse(record.recordDate)
                    .format(
                        DateTimeFormatter.ofLocalizedDate(
                            FormatStyle.FULL,
                        ),
                    )
            },
            selectedInitial = uiState.currentWeek.values.find {
                it.recordDate == LocalDate.now().toString()
            },
        )

        WalkingProgressGraphContainer(
            data = uiState.lastWeeks.values.toList(),
            title = stringResource(R.string.weekly_progress),
            selectedValueTitle = { record ->
                val startDate = LocalDate.parse(record.recordDate)
                val endDate = startDate.plusDays(6)

                "$startDate - $endDate"
            },
            selectedInitial = uiState.lastWeeks.values.lastOrNull(),
        )
    }
}

@Composable
private fun WalkingProgressGraphContainer(
    data: List<DailyStepRecord>,
    title: String,
    xAxisLabels: List<String> = emptyList(),
    selectedValueTitle: (DailyStepRecord) -> String,
    selectedInitial: DailyStepRecord? = data.lastOrNull(),
) {
    val selected = remember {
        mutableStateOf(selectedInitial)
    }

    val graphTitle = if (selected.value != null) {
        selectedValueTitle(selected.value!!)
    } else {
        title
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = graphTitle,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
        )

        DailyStepRecordTexts(selected = selected)

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            ProgressGraph(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                data = data
                    .map { it.totalSteps }
                    .ifEmpty {
                        List(xAxisLabels.size) { 0 }
                    },
                xAxisLabels = xAxisLabels,
                onSelectedIndexChange = { index ->
                    selected.value = data.getOrNull(index)
                },
                selected = data.indexOf(selected.value),
            )
        }
    }
}

@Composable
private fun DailyStepRecordTexts(selected: MutableState<DailyStepRecord?>) {
    val record = selected.value ?: DailyStepRecord()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        TextWithLabel(
            label = "Steps",
            text = record.totalSteps.toString(),
        )

        VerticalDivider(
            modifier = Modifier
                .height(18.dp)
                .padding(horizontal = 12.dp),
            thickness = 1.dp,
        )

        TextWithLabel(
            label = "Calories",
            text = record.caloriesBurned?.toString() ?: "0",
        )

        VerticalDivider(
            modifier = Modifier
                .height(18.dp)
                .padding(horizontal = 12.dp),
            thickness = 1.dp,
        )

        TextWithLabel(
            label = "Distance",
            text = String.format("%.2f", record.totalDistance),
        )
    }
}