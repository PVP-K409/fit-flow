package com.github.k409.fitflow.ui.components.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.ui.common.noRippleClickable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarView(
    selectedDate: MutableState<LocalDate>,
    weeksCount: Int = 52,
    onSelectedDateChanged: (LocalDate) -> Unit = {}
) {
    val today = LocalDate.now()
    val weeks = getWeeksFromToday(today, weeksCount)

    val pagerState = rememberPagerState(
        initialPage = weeks.lastIndex,
        initialPageOffsetFraction = 0f
    ) { weeks.size }

    Column(modifier = Modifier.fillMaxWidth()) {
        val dateName =
            selectedDate.value.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))

        Text(
            text = dateName,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val weekDates = weeks[page]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                weekDates.forEach { date ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .noRippleClickable(
                                    onClick = {
                                        selectedDate.value = date
                                        onSelectedDateChanged(date)
                                    }
                                )
                                .background(
                                    if (date == selectedDate.value)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                textAlign = TextAlign.Center
                            )
                        }

                    }
                }
            }
        }
    }
}

fun getWeeksFromToday(
    today: LocalDate,
    weeksCount: Int
): List<List<LocalDate>> {
    val weeks = mutableListOf<List<LocalDate>>()
    var currentStartOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)

    repeat(weeksCount) {
        val week = (0 until 7).map { currentStartOfWeek.plusDays(it.toLong()) }
        weeks.add(0, week)

        currentStartOfWeek = currentStartOfWeek.minusWeeks(1)
    }

    return weeks
}