package com.github.k409.fitflow.ui.screen.hydration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.HydrationRecord
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HydrationLogsPage(viewModel: HydrationViewModel) {
    val hydrationRecords by viewModel.hydrationLogsUiState.collectAsState()

    when (hydrationRecords) {
        is HydrationLogsUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }

        is HydrationLogsUiState.Success -> {
            HydrationLogsContent(uiState = hydrationRecords as HydrationLogsUiState.Success)
        }
    }
}

@Composable
private fun HydrationLogsContent(uiState: HydrationLogsUiState.Success) {
    val groupedRecords = uiState.groupedRecords

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") {
            Text(
                modifier = Modifier
                    .fillMaxSize(),
                text = stringResource(R.string.hydration_logs),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start
            )
        }

        groupedRecords.forEach { (date, logs) ->
            item(key = date) {
                Text(
                    modifier = Modifier
                        .fillMaxSize(),
                    text = formatDate(date),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )
            }

            items(
                items = logs,
                key = { it.date }
            ) { hydrationRecord ->
                HydrationRecordCard(
                    hydrationRecord = hydrationRecord,
                    goal = 500,
                )
            }

        }
    }
}

@Composable
private fun HydrationRecordCard(
    modifier: Modifier = Modifier,
    hydrationRecord: HydrationRecord,
    goal: Number,
) {
    val progress = (hydrationRecord.waterIntake.toFloat() / goal.toFloat()).coerceIn(0f, 1f)

    val goalText = buildAnnotatedString {
        withStyle(
            SpanStyle(fontWeight = FontWeight.Bold)
        ) {
            append("${hydrationRecord.waterIntake}")
        }
        append(" / $goal ml")
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                imageVector = if (progress >= 1) Icons.Filled.WaterDrop else Icons.Outlined.WaterDrop,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )

            Column {
                Row {
                    Text(
                        text = hydrationRecord.date,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${(progress * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LinearProgressIndicator(
                        trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )
                    Text(
                        modifier = Modifier.widthIn(min = 80.dp),
                        text = goalText,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }

    }
}

private fun formatDate(date: String): String {
    val yearPart = date.substring(0, 4)
    val monthPart = date.substring(5, 7)

    val month = Month.of(monthPart.toInt())
        .getDisplayName(
            TextStyle.FULL,
            Locale.getDefault()
        )

    return "$month $yearPart"
}
