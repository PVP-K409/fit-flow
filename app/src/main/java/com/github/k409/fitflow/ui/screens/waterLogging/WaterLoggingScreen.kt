package com.github.k409.fitflow.ui.screens.waterLogging

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun WaterLoggingScreen(userWeight: Int){

    val context = LocalContext.current

    var waterIntake by remember { mutableIntStateOf(0) }
    val dailyGoal = userWeight*30


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
            onClick = { waterIntake += 250
                        WaterReminder().scheduleWaterReminder(context)
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Drink 250ml")
        }

        LinearProgressIndicator(
            progress = { waterIntake.toFloat() / dailyGoal.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total water drunk: $waterIntake ml",
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

@Composable
fun GetWeight() {
    val context = LocalContext.current
    val usersRef = FirebaseFirestore.getInstance().collection("users")
    val currentUser = FirebaseAuth.getInstance().currentUser
    var userWeight by remember { mutableIntStateOf(0) }

    if (currentUser != null) {
        val uid = currentUser.uid
        usersRef.document(uid).get().addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val weight = document.getLong("weight")
                    if (weight != null) {
                        userWeight = weight.toInt()
                    }
                } else{
                    Log.d(TAG, "The document doesn't exist.")
                }
            } else {
                task.exception?.message?.let {
                    Log.d(TAG, it)
                }
            }
        }
    }
    WaterLoggingScreen(userWeight)
}

//@Preview
//@Composable
//fun ComposablePreview() {
//    WaterLoggingScreen()
//}