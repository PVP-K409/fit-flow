package com.github.k409.fitflow.features.step_counter

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class StepCounter(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val prefs: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    suspend fun steps() = suspendCancellableCoroutine { continuation ->

        val listener: SensorEventListener by lazy {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return

                    val stepsSinceLastReboot = event.values[0].toLong() // Steps since last reboot

                    if (stepsSinceLastReboot.toInt() <= 1) { // if reboot has happened
                        val editor = prefs.edit()

                        editor.putBoolean("rebooted", true)

                        val wasSuccessful = editor.commit()
                    }

                    sensorManager.unregisterListener(this)

                    if (continuation.isActive) {
                        continuation.resume(stepsSinceLastReboot)
                    }

                }

                override fun onAccuracyChanged(
                    sensor: Sensor?,
                    accuracy: Int
                ) {
                    //not needed
                }
            }
        }

        sensorManager.registerListener(
            listener,
            sensor, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

}