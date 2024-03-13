package com.github.k409.fitflow.ui.screens.waterLogging

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.k409.fitflow.ui.components.hydration.WaterIntake
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun saveCustomAmountLocally(key: String, value: Int, context: Context) {

    // Access the default SharedPreferences file
    val sharedPref = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)

    with (sharedPref.edit()) {
        putInt(key, value)
        apply()
    }
}

fun getSavedAmount(key: String, context: Context): Int {
    val sharedPref = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
    // Retrieve the value associated with the key, or 0 if not found
    return sharedPref.getInt(key, 0)
}

@Composable
fun WaterLoggingScreen() {

    val context = LocalContext.current
    val dailyGoal = getWaterIntakeGoal()
    val scope = rememberCoroutineScope()

    val key1 = "waterAmount1"
    val key2 = "waterAmount2"
    val key3 = "waterAmount3"

    var button1Value by remember { mutableIntStateOf(0) }
    var button2Value by remember { mutableIntStateOf(0) }
    var button3Value by remember { mutableIntStateOf(0) }

    var totalWaterIntake by remember { mutableIntStateOf(0) }
    var yesterdaysTotalWaterIntake by remember { mutableIntStateOf(0) }
    var thisWeeksTotalWaterIntake by remember { mutableIntStateOf(0) }
    var thisMonthsTotalWaterIntake by remember { mutableIntStateOf(0) }

    var customAmountValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {

        button1Value = getSavedAmount(key1, context)
        button2Value = getSavedAmount(key2, context)
        button3Value = getSavedAmount(key3, context)

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
                text = "$totalWaterIntake ml / $dailyGoal ml",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

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

//Custom Buttons=================================================================================

        TextField(
            value = customAmountValue,
            onValueChange = { newValue ->
                customAmountValue = newValue.filter { it.isDigit() }
            },
            trailingIcon = {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "clear text",
                    modifier = Modifier
                        .clickable {
                            customAmountValue = ""
                        }
                )
            },
            label = { Text("Custom Amount (ml)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if(customAmountValue.isNotBlank()){
                    saveCustomAmountLocally(key = key1,
                        value = customAmountValue.toInt(),
                        context = context)

                    button1Value = getSavedAmount(key1, context)
                } else {
                    scope.launch {
                        addWaterIntake(button1Value)
                        delay(180)
                        totalWaterIntake = retrieveTotalWaterIntake()
                        yesterdaysTotalWaterIntake = retrieveWaterIntakeYesterday()
                        thisWeeksTotalWaterIntake = retrieveWaterIntakeThisWeek()
                        thisMonthsTotalWaterIntake = retrieveWaterIntakeThisMonth()
                    }
                    WaterReminder().scheduleWaterReminder(context)
                }
            }
        ) {
            if(button1Value == 0){
                Text("Not set")
            }else{
                Text("Drink $button1Value ml")
            }
        }

        Button(
            onClick = {
                if(customAmountValue.isNotBlank()){
                    saveCustomAmountLocally(key = key2,
                        value = customAmountValue.toInt(),
                        context = context)

                    button2Value = getSavedAmount(key2, context)
                } else {
                    scope.launch {
                        addWaterIntake(button2Value)
                        delay(180)
                        totalWaterIntake = retrieveTotalWaterIntake()
                        yesterdaysTotalWaterIntake = retrieveWaterIntakeYesterday()
                        thisWeeksTotalWaterIntake = retrieveWaterIntakeThisWeek()
                        thisMonthsTotalWaterIntake = retrieveWaterIntakeThisMonth()
                    }
                    WaterReminder().scheduleWaterReminder(context)
                }
            }
        ) {
            if(button2Value == 0){
                Text("Not set")
            }else{
                Text("Drink $button2Value ml")
            }
        }

        Button(
            onClick = {
                if(customAmountValue.isNotBlank()){
                    saveCustomAmountLocally(key = key3,
                        value = customAmountValue.toInt(),
                        context = context)

                    button3Value = getSavedAmount(key3, context)
                } else {
                    scope.launch {
                        addWaterIntake(button3Value)
                        delay(180)
                        totalWaterIntake = retrieveTotalWaterIntake()
                        yesterdaysTotalWaterIntake = retrieveWaterIntakeYesterday()
                        thisWeeksTotalWaterIntake = retrieveWaterIntakeThisWeek()
                        thisMonthsTotalWaterIntake = retrieveWaterIntakeThisMonth()
                    }
                    WaterReminder().scheduleWaterReminder(context)
                }
            }
        ) {
            if(button3Value == 0){
                Text("Not set")
            }else{
                Text("Drink $button3Value ml")
            }
        }
//===============================================================================================

        WaterIntake(milliliters = yesterdaysTotalWaterIntake.toLong(),
            thisWeek = thisWeeksTotalWaterIntake.toDouble(),
            thisMonth = thisMonthsTotalWaterIntake.toDouble())
    }
}
