package com.github.k409.fitflow.ui.step_counter

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import android.hardware.SensorEvent
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class StepCounter(context: Context){
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    suspend fun steps() = suspendCancellableCoroutine { continuation ->

        val listener: SensorEventListener by lazy {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return

                    val stepsSinceLastReboot = event.values[0].toLong() // Steps since last reboot
                    Log.d("Step Detected", "Step count - $stepsSinceLastReboot")


                    if (continuation.isActive) {
                        continuation.resume(stepsSinceLastReboot)
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    //not needed
                }
            }
        }

        val supportedAndEnabled = sensorManager.registerListener(listener,
            sensor, SensorManager.SENSOR_DELAY_NORMAL) // registered sensor listener
    }

}