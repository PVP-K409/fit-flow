package com.github.k409.fitflow.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.ui.common.ConfirmDialog

@Composable
fun SwitchSettingEntry(
    title: String,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    SettingsEntry(
        title = title,
        text = text,
        isEnabled = isEnabled,
        onClick = { onCheckedChange(!isChecked) },
        trailingContent = {
            Switch(
                enabled = isEnabled,
                checked = isChecked,
                onCheckedChange = { onCheckedChange(!isChecked) },
            )
        },
        modifier = modifier,
    )
}

@Composable
fun SettingsEntry(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    confirmClick: Boolean = false,
    isEnabled: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                enabled = isEnabled,
                onClick = {
                    if (confirmClick) {
                        showDialog = true
                    } else {
                        onClick()
                    }
                },
            )
            .alpha(if (isEnabled) 1f else 0.5f)
            .padding(
                start = 16.dp,
                end = 16.dp,
            )
            .padding(all = 16.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        trailingContent?.invoke()
    }

    if (showDialog) {
        ConfirmDialog(
            dialogTitle = "Are you sure?",
            dialogText = "This action cannot be undone",
            onConfirm = {
                showDialog = false
                onClick()
            },
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
fun SettingsDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
            .padding(start = 16.dp)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
    )
}

@Composable
fun SettingsEntryGroupText(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
            .padding(start = 16.dp)
            .padding(horizontal = 16.dp),
    )
}

@Composable
fun SettingsGroupSpacer(
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier.height(24.dp),
    )
}

@Composable
fun ImportantSettingsDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
            .padding(start = 16.dp)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
    )
}
