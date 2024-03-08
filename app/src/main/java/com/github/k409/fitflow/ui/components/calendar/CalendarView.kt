package com.github.k409.fitflow.ui.components.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.ui.common.noRippleClickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarView(
    selectedDate: MutableState<LocalDate>,
    weeksCount: Int = 52,
) {
    val today = LocalDate.now()
    val weeks = getWeeksFromToday(today, weeksCount)

    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = weeks.lastIndex, initialPageOffsetFraction = 0f
    ) { weeks.size }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val newWeek = weeks[page]
            val newSelectedDate = newWeek.find { it.dayOfWeek == selectedDate.value.dayOfWeek }

            if (newSelectedDate != null) {
                selectedDate.value = newSelectedDate
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        CalendarHeader(
            selectedDate = selectedDate,
            coroutineScope = coroutineScope,
            pagerState = pagerState,
            today = today,
            weeks = weeks
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
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light)
                )
            }
        }

        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxWidth()
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
                                .noRippleClickable(onClick = {
                                    selectedDate.value = date
                                })
                                .background(
                                    if (date == selectedDate.value) MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.1f
                                    )
                                    else Color.Transparent
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (date == selectedDate.value) MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.2f
                                    )
                                    else Color.Transparent,
                                    shape = CircleShape
                                ), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(), textAlign = TextAlign.Center
                            )
                        }

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CalendarHeader(
    selectedDate: MutableState<LocalDate>,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    today: LocalDate,
    weeks: List<List<LocalDate>>
) {
    val dateName =
        selectedDate.value.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (pagerState.currentPage != 0) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(16.dp)
                    .alpha(0.5f)
                    .noRippleClickable(onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }),
                imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                contentDescription = null,
            )
        }

        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .noRippleClickable(
                        onClick = {
                            selectedDate.value = today

                            coroutineScope.launch {
                                pagerState.scrollToPage(weeks.lastIndex)
                            }
                        }),
                imageVector = Icons.Outlined.Today,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = dateName,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
            )
        }


        if (pagerState.currentPage != weeks.lastIndex) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(16.dp)
                    .alpha(0.5f)
                    .noRippleClickable(onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }),
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos, contentDescription = ""
            )
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