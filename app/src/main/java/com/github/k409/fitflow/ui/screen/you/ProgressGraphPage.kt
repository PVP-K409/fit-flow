package com.github.k409.fitflow.ui.screen.you

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.DailyStepRecord
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.ProgressGraph
import com.github.k409.fitflow.ui.common.TextLabelWithDivider
import com.github.k409.fitflow.ui.common.TextWithLabel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
internal fun ProgressGraphPage(
    youViewModel: YouViewModel,
) {
    val progressUiState by youViewModel.progressUiState.collectAsState()

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
private fun ProgressGraphPageContent(
    uiState: ProgressUiState.Success,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ThisMonthSection(uiState = uiState)

        WeeklySection(uiState = uiState)
    }
}

@Composable
private fun WeeklySection(uiState: ProgressUiState.Success) {
    SectionHeaderCard(
        title = stringResource(id = R.string.weekly),
        subtitle = stringResource(R.string.last_12_weeks),
    )

    // Current Week Progress
    WalkingProgressGraphContainer(
        data = uiState.currentWeek.values.toList(),
        title = stringResource(R.string.current_week_progress),
        subtitle = stringResource(R.string.your_progress_for_this_week),
        xAxisLabels = uiState.currentWeek.keys.toList(),
        selectedValueTitle = { record ->
            LocalDate.parse(record.recordDate).format(
                DateTimeFormatter.ofLocalizedDate(
                    FormatStyle.FULL,
                ),
            )
        },
        selectedInitial = uiState.currentWeek.values.find {
            it.recordDate == LocalDate.now().toString()
        },
    )

    // Last 12 Weeks Progress
    WalkingProgressGraphContainer(
        data = uiState.lastWeeks.values.toList(),
        title = stringResource(R.string.weekly_progress),
        subtitle = stringResource(R.string.your_progress_for_the_last_12_weeks),
        selectedValueTitle = { record ->
            val startDate = LocalDate.parse(record.recordDate)
            val endDate = startDate.plusDays(6)

            "$startDate - $endDate"
        },
        selectedInitial = uiState.lastWeeks.values.lastOrNull(),
    )
}

@Composable
private fun ThisMonthSection(uiState: ProgressUiState.Success) {
    val now = LocalDate.now()
    val currentMonth = now.month.toString().lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    SectionHeaderCard(
        title = currentMonth,
        subtitle = now.year.toString(),
    )

    StatisticsCard(uiState = uiState)

    BestEffortsCard(uiState = uiState)

    // Current Month Progress
    WalkingProgressGraphContainer(
        data = uiState.thisMonth.values.toList(),
        title = stringResource(R.string.current_month_progress),
        subtitle = stringResource(R.string.your_progress_for_this_month),
        selectedValueTitle = { record ->
            LocalDate.parse(record.recordDate).format(
                DateTimeFormatter.ofLocalizedDate(
                    FormatStyle.FULL,
                ),
            )
        },
        selectedInitial = uiState.thisMonth.values.find {
            it.recordDate == LocalDate.now().toString()
        },
    )
}

@Composable
private fun BestEffortsCard(uiState: ProgressUiState.Success) {
    val title = stringResource(R.string.best_efforts_title)
    val subtitleText = stringResource(R.string.best_efforts_subtitle)

    val maxStepsEntry = uiState.thisMonth.maxByOrNull { it.value.totalSteps }
    val mostCaloriesEntry = uiState.thisMonth.maxByOrNull { it.value.caloriesBurned ?: 0 }
    val mostDistanceEntry = uiState.thisMonth.maxByOrNull { it.value.totalDistance ?: 0.0 }

    val maxHydrationEntry = uiState.hydrationStats.maxIntake

    OutlineCardContainer(
        title = title,
        subtitleText = subtitleText,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            EffortRow(
                leftLabel = stringResource(R.string.most_steps),
                leftValue = formatDate(maxStepsEntry?.key),
                rightLabel = (maxStepsEntry?.value?.totalSteps?.toString() ?: "0"),
                rightValue = stringResource(R.string.steps).lowercase(),
                iconImageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
            )

            HorizontalDivider()

            EffortRow(
                leftLabel = stringResource(R.string.most_calories),
                leftValue = formatDate(mostCaloriesEntry?.key),
                rightLabel = (mostCaloriesEntry?.value?.caloriesBurned?.toString() ?: "0"),
                rightValue = stringResource(R.string.kcal),
                iconImageVector = Icons.Default.LocalFireDepartment,
            )

            HorizontalDivider()

            EffortRow(
                leftLabel = stringResource(R.string.most_distance),
                leftValue = formatDate(mostDistanceEntry?.key),
                rightLabel = String.format(
                    Locale.getDefault(),
                    "%.2f",
                    mostDistanceEntry?.value?.totalDistance ?: 0f,
                ),
                rightValue = stringResource(R.string.km),
                iconImageVector = Icons.Default.Straighten,
            )

            HorizontalDivider()

            // most hydration
            EffortRow(
                leftLabel = stringResource(R.string.most_hydration),
                leftValue = formatDate(maxHydrationEntry.first),
                rightLabel = String.format(
                    Locale.getDefault(),
                    "%.2f",
                    maxHydrationEntry.second / 1000f,
                ),
                rightValue = stringResource(R.string.litres),
                iconImageVector = Icons.Default.WaterDrop,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SectionHeaderCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
) {
    OutlinedCard(
        modifier = modifier
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
            )

            subtitle?.let {
                Text(
                    modifier = Modifier.padding(top = 6.dp, bottom = 0.dp),
                    text = it,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.W300,
                )
            }
        }
    }
}

@Composable
private fun StatisticsCard(uiState: ProgressUiState.Success) {
    val title = stringResource(R.string.statistics_title)
    val subtitleText = stringResource(R.string.statistics_subtitle)

    val totalSteps = uiState.thisMonth.values.sumOf { it.totalSteps }
    val totalCalories = uiState.thisMonth.values.sumOf { it.caloriesBurned ?: 0 }
    val totalDistance = uiState.thisMonth.values.sumOf { it.totalDistance ?: 0.0 }

    val hydrationMonth = uiState.hydrationStats.thisMonthTotalAmount / 1000f
    val avgHydration = hydrationMonth / 30f

    OutlineCardContainer(
        title = title,
        subtitleText = subtitleText,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            EffortRow(
                leftLabel = stringResource(R.string.total_steps),
                leftValue = stringResource(R.string.this_month),
                rightLabel = totalSteps.toString(),
                rightValue = stringResource(R.string.steps).lowercase(),
                iconImageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
            )

            HorizontalDivider()

            EffortRow(
                leftLabel = stringResource(R.string.total_calories),
                leftValue = stringResource(R.string.this_month),
                rightLabel = totalCalories.toString(),
                rightValue = stringResource(R.string.kcal),
                iconImageVector = Icons.Default.LocalFireDepartment,
            )

            HorizontalDivider()

            EffortRow(
                leftLabel = stringResource(R.string.total_distance),
                leftValue = stringResource(R.string.this_month),
                rightLabel = String.format(Locale.getDefault(), "%.2f", totalDistance),
                rightValue = stringResource(R.string.km),
                iconImageVector = Icons.Default.Straighten,
            )

            HorizontalDivider()

            EffortRow(
                leftLabel = stringResource(R.string.total_hydration),
                leftValue = stringResource(R.string.this_month),
                rightLabel = String.format(Locale.getDefault(), "%.2f", hydrationMonth),
                rightValue = stringResource(R.string.litres),
                iconImageVector = Icons.Default.WaterDrop,
            )

            HorizontalDivider()

            EffortRow(
                leftLabel = stringResource(R.string.avg_hydration),
                leftValue = stringResource(R.string.per_day),
                rightLabel = String.format(Locale.getDefault(), "%.2f", avgHydration),
                rightValue = stringResource(R.string.litres),
                iconImageVector = Icons.Outlined.LocalDrink,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun EffortRow(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String,
    iconImageVector: ImageVector = Icons.Default.FitnessCenter,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = iconImageVector,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp),
            )

            TextWithLabel(
                label = leftLabel,
                horizontalAlignment = Alignment.Start,
                text = leftValue,
                labelStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                textStyle = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }

        TextWithLabel(
            label = rightLabel,
            horizontalAlignment = Alignment.End,
            text = rightValue,
            labelStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            textStyle = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}

@Composable
fun OutlineCardContainer(
    modifier: Modifier = Modifier,
    title: String,
    subtitleText: String,
    spacerHeight: Dp = 16.dp,
    content: @Composable () -> Unit = {},
) {
    OutlinedCard(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                modifier = Modifier.padding(bottom = 6.dp),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = subtitleText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Light,
            )

            Spacer(modifier = Modifier.height(spacerHeight))

            content()
        }
    }
}

@Composable
private fun WalkingProgressGraphContainer(
    data: List<DailyStepRecord>,
    title: String,
    subtitle: String,
    xAxisLabels: List<String> = emptyList(),
    selectedValueTitle: (DailyStepRecord) -> String,
    selectedInitial: DailyStepRecord? = data.lastOrNull(),
) {
    var selectedRecord by remember {
        mutableStateOf(selectedInitial)
    }

    val subtitleText = if (selectedRecord != null) {
        selectedValueTitle(selectedRecord!!)
    } else {
        subtitle
    }

    OutlinedCard(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                modifier = Modifier.padding(bottom = 6.dp),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = subtitleText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Light,
            )

            TextLabelWithDivider(
                data = listOf(
                    stringResource(id = R.string.steps) to (selectedRecord?.totalSteps ?: 0),
                    stringResource(id = R.string.calories) to "${(selectedRecord?.caloriesBurned ?: 0)} kcal",
                    stringResource(id = R.string.distance) to String.format(
                        Locale.getDefault(),
                        "%.2f km",
                        selectedRecord?.totalDistance ?: 0f,
                    ),
                ),
                horizontalArrangement = Arrangement.Start,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                ProgressGraph(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    data = data.map { it.totalSteps }.ifEmpty {
                        List(xAxisLabels.size) { 0 }
                    },
                    xAxisLabels = xAxisLabels,
                    onSelectedIndexChange = { index ->
                        selectedRecord = data.getOrNull(index)
                    },
                    selected = data.indexOf(selectedRecord),
                )
            }
        }
    }
}

private fun formatDate(
    date: String?,
    formatStyle: FormatStyle = FormatStyle.MEDIUM,
): String {
    if (date.isNullOrEmpty()) {
        return ""
    }

    return LocalDate.parse(date).format(
        DateTimeFormatter.ofLocalizedDate(
            formatStyle,
        ),
    )
}
