package com.github.k409.fitflow.ui.step_counter

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import android.content.SharedPreferences
import android.hardware.SensorEvent
import android.icu.util.Calendar
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import javax.inject.Singleton

@Singleton
class StepCounter(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
    val todayStepsLiveData = MutableLiveData<Int>()
    private var todaySteps = 0 // Today's steps, resets every new day

    init {
        val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            Toast.makeText(context, "Step detector sensor not detected on this device.", Toast.LENGTH_SHORT).show()
        }
        checkForNewDay()
    }

    private fun checkForNewDay() {
        val savedDayOfYear = sharedPreferences.getInt("savedDayOfYear", -1)
        val currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        if (currentDayOfYear != savedDayOfYear) {
            // It's a new day
            todaySteps = 0 // Reset the step count for the new day
            sharedPreferences.edit().apply {
                putInt("savedDayOfYear", currentDayOfYear)
                putInt("todaySteps", todaySteps)
                apply()
            }
        } else {
            // It's the same day, load the saved step count
            todaySteps = sharedPreferences.getInt("todaySteps", 0)
        }
        todayStepsLiveData.postValue(todaySteps) // Update LiveData with the current or 0
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            checkForNewDay() // We check for new day

            todaySteps++ // Increment and save the step count for each detected step
            todayStepsLiveData.postValue(todaySteps)
            sharedPreferences.edit().putInt("todaySteps", todaySteps).apply()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }

    fun unregisterListener() {
        sensorManager.unregisterListener(this)
    }
}