package com.github.k409.fitflow.di

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthClientModule {

    @Provides
    @Singleton
    fun provideHealthClient(@ApplicationContext context: Context): HealthConnectClient {
        val provider = "com.google.android.apps.healthdata"
        val status = HealthConnectClient.getSdkStatus(context, provider)

        if (status == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) { // under API level 14
            val uriString =
                "market://details?id=$provider&url=healthconnect%3A%2F%2Fonboarding"
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)

                    setPackage("com.android.vending")

                    data = Uri.parse(uriString)

                    putExtra("overlay", true)
                    putExtra("callerId", context.packageName)
                },
            )
        }

        return HealthConnectClient.getOrCreate(context)
    }
}
