package com.github.k409.fitflow.ui.common

import android.widget.NumberPicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.k409.fitflow.R

@Composable
fun ConfirmDialog(
    dialogTitle: String,
    dialogText: String,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}

@Composable
fun Dialog(
    title: String,
    saveButtonTitle: String = "OK",
    onSaveClick: () -> Unit,
    onDismiss: () -> Unit,
    dismissButtonTitle: String = "Cancel",
    buttonsVisible: Boolean = true,
    content: @Composable () -> Unit,
) {
    Dialog(
        title = title,
        buttonsVisible = buttonsVisible,
        selectedValue = null,
        onSaveClick = { onSaveClick() },
        onDismiss = onDismiss,
        saveButtonTitle = saveButtonTitle,
        dismissButtonTitle = dismissButtonTitle,
    ) {
        content()
    }
}

@Composable
fun <T> Dialog(
    title: String,
    buttonsVisible: Boolean = true,
    selectedValue: T,
    onSaveClick: (T) -> Unit,
    saveButtonTitle: String = "OK",
    onDismiss: () -> Unit,
    dismissButtonTitle: String = "Cancel",
    content: @Composable () -> Unit,
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(30.dp))
                content()
                Spacer(modifier = Modifier.height(30.dp))

                if (buttonsVisible) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TextButton(
                            onClick = onDismiss,
                        ) {
                            Text(text = dismissButtonTitle)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        TextButton(onClick = { onSaveClick(selectedValue) }) {
                            Text(text = saveButtonTitle)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPickerDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    dialogText: String,
    minValue: Int,
    maxValue: Int,
    initialValue: Int,
    displayedValues: Array<String>?,
) {
    var currentValue = initialValue
    val themeColor = MaterialTheme.colorScheme.onSurface
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
            // initialize number picker widget
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    NumberPicker(context)
                },
                update = { numberPicker ->
                    numberPicker.apply {
                        setOnValueChangedListener { _, _, newValue ->
                            currentValue = newValue
                        }
                        this.minValue = minValue
                        this.maxValue = maxValue
                        this.value = initialValue
                        if (displayedValues != null) {
                            this.displayedValues = displayedValues
                        }
                        // Disable manual user input on number pickers
                        // this.descendantFocusability = FOCUS_BLOCK_DESCENDANTS
                        // Needs API 29+
                        this.textColor = themeColor.toArgb()
                    }
                },
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(currentValue.toString())
                },
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
