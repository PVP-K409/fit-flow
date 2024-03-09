package com.github.k409.fitflow.ui.screens.waterLogging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WaterLoggingScreen(){

    val context = LocalContext.current
    val dailyGoal = getWaterIntakeGoal()
    val scope = rememberCoroutineScope()

    var totalWaterIntake by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        addWaterIntake(0)
        totalWaterIntake = retrieveTotalWaterIntake()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = "Water intake logging",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    addWaterIntake(250)
                    delay(150)
                    totalWaterIntake = retrieveTotalWaterIntake()
                }
                        WaterReminder().scheduleWaterReminder(context)
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Drink 250ml")
        }

        LinearProgressIndicator(
            progress = { totalWaterIntake.toFloat() / dailyGoal.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total water drunk: $totalWaterIntake ml",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Goal: $dailyGoal ml",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}