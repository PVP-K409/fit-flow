package com.github.k409.fitflow.ui.widget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object WidgetInfoDefinition : GlanceStateDefinition<WidgetInfo> {

    private const val DATA_STORE_FILENAME = "widgetState"

    private val Context.datastore by dataStore(DATA_STORE_FILENAME, WidgetInfoSerializer)
    override suspend fun getDataStore(
        context: Context,
        fileKey: String,
    ): DataStore<WidgetInfo> {
        return context.datastore
    }

    override fun getLocation(
        context: Context,
        fileKey: String,
    ): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    object WidgetInfoSerializer : Serializer<WidgetInfo> {
        override val defaultValue = WidgetInfo.Loading

        override suspend fun readFrom(input: InputStream): WidgetInfo = try {
            Json.decodeFromString(
                WidgetInfo.serializer(),
                input.readBytes().decodeToString(),
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read data: ${exception.message}")
        }

        override suspend fun writeTo(
            t: WidgetInfo,
            output: OutputStream,
        ) {
            output.use {
                it.write(
                    Json.encodeToString(WidgetInfo.serializer(), t).encodeToByteArray(),
                )
            }
        }
    }
}
