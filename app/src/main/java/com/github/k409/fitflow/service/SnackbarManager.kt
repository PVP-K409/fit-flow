package com.github.k409.fitflow.service

import android.content.res.Resources
import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

sealed class SnackbarMessage(val id: Long = UUID.randomUUID().mostSignificantBits) {
    class StringSnackbar(val message: String) : SnackbarMessage()
    class ResourceSnackbar(@StringRes val message: Int) : SnackbarMessage()

    companion object {
        fun SnackbarMessage.toMessage(resources: Resources): String {
            return when (this) {
                is StringSnackbar -> this.message
                is ResourceSnackbar -> resources.getString(this.message)
            }
        }
    }
}

/**
 * Class responsible for managing Snackbar messages to show on the screen
 */
object SnackbarManager {

    private val _currentMessage: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val currentMessage: StateFlow<SnackbarMessage?> get() = _currentMessage.asStateFlow()

    private val _messages: MutableStateFlow<List<SnackbarMessage>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<SnackbarMessage>> get() = _messages.asStateFlow()

    fun showMessage(message: String) {
        _messages.update { currentMessages ->
            currentMessages + SnackbarMessage.StringSnackbar(message)
        }
    }

    fun showMessage(@StringRes message: Int) {
        _messages.update { currentMessages ->
            currentMessages + SnackbarMessage.ResourceSnackbar(message)
        }
    }

    fun showNotDuplicateMessage(message: String) {
        if (currentMessage.value is SnackbarMessage.StringSnackbar && (currentMessage.value as SnackbarMessage.StringSnackbar).message == message) {
            return
        }

        showMessage(message)
    }

    fun showNotDuplicateMessage(@StringRes message: Int) {
        if (currentMessage.value is SnackbarMessage.ResourceSnackbar && (currentMessage.value as SnackbarMessage.ResourceSnackbar).message == message) {
            return
        }

        showMessage(message)
    }

    fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }

    fun setCurrentMessage(message: SnackbarMessage?) {
        _currentMessage.value = message
    }
}
