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

    // Holds the total step count at the start of the app or day.
    private var initialSensorValue = sharedPreferences.getFloat("initialSensorValue", 0f)
    private var hasInitializedSensorValue = sharedPreferences.getBoolean("hasInitializedSensorValue", false)

    init {
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            Toast.makeText(context,"No sensor detected on this device", Toast.LENGTH_SHORT).show()
        }
        InitializeAndCheckEdge()
    }

    private fun InitializeAndCheckEdge() {
        checkForNewDay()
    }

    private fun checkForNewDay() {
        val currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val savedDayOfYear = sharedPreferences.getInt("savedDayOfYear", -1)

        if (currentDayOfYear != savedDayOfYear) {
            // It's a new day or app first launch, reset initial sensor value flag
            hasInitializedSensorValue = false
            sharedPreferences.edit().apply {
                putInt("savedDayOfYear", currentDayOfYear)
                putBoolean("hasInitializedSensorValue", false)
                apply()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (!hasInitializedSensorValue) {
                // Initialize with the current total step count from the sensor
                initialSensorValue = event.values[0]
                hasInitializedSensorValue = true
                sharedPreferences.edit().apply {
                    putFloat("initialSensorValue", initialSensorValue)
                    putBoolean("hasInitializedSensorValue", true)
                    apply()
                }
            }
            checkForNewDay()

            val totalSteps = event.values[0]
            val todaySteps = (totalSteps - initialSensorValue).toInt()
            todayStepsLiveData.postValue(todaySteps)

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }

    fun unregisterListener() {
        sensorManager.unregisterListener(this)
    }
}