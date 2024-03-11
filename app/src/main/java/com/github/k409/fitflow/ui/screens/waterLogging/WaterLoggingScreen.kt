package com.github.k409.fitflow.ui.screens.waterLogging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.k409.fitflow.ui.components.hydration.WaterIntake
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WaterLoggingScreen() {

    val context = LocalContext.current
    val dailyGoal = getWaterIntakeGoal()
    val scope = rememberCoroutineScope()

    var totalWaterIntake by remember { mutableIntStateOf(0) }
    var yesterdaysTotalWaterIntake by remember { mutableIntStateOf(0) }
    var thisWeeksTotalWaterIntake by remember { mutableIntStateOf(0) }
    var thisMonthsTotalWaterIntake by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        createHydrationDocument()
        delay(600)
        addWaterIntake(0)
        delay(180)
        totalWaterIntake = retrieveTotalWaterIntake()
        yesterdaysTotalWaterIntake = retrieveWaterIntakeYesterday()
        thisWeeksTotalWaterIntake = retrieveWaterIntakeThisWeek()
        thisMonthsTotalWaterIntake = retrieveWaterIntakeThisMonth()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Water intake logging",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                progress = { totalWaterIntake.toFloat() / dailyGoal.toFloat() },
                modifier = Modifier
                    .height(200.dp)
                    .aspectRatio(1f)
                    .padding(16.dp),
                trackColor = Color(android.graphics.Color.parseColor("#AFE6F0")),
                strokeWidth = 20.dp,
                color = Color(android.graphics.Color.parseColor("#03768A"))
            )

            Text(
                text = "Total water drunk: $totalWaterIntake ml",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Goal: $dailyGoal ml",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    addWaterIntake(250)
                    delay(180)
                    totalWaterIntake = retrieveTotalWaterIntake()
                    yesterdaysTotalWaterIntake = retrieveWaterIntakeYesterday()
                    thisWeeksTotalWaterIntake = retrieveWaterIntakeThisWeek()
                    thisMonthsTotalWaterIntake = retrieveWaterIntakeThisMonth()
                }
                WaterReminder().scheduleWaterReminder(context)
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Drink 250ml")
        }
        WaterIntake(milliliters = yesterdaysTotalWaterIntake.toLong(),
            thisWeek = thisWeeksTotalWaterIntake.toDouble(),
            thisMonth = thisMonthsTotalWaterIntake.toDouble())
    }
}
