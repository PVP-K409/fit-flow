package com.github.k409.fitflow.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight


@Composable
fun TextWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
    labelStyle: TextStyle = MaterialTheme.typography.labelMedium,

    ) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = labelStyle,
        )
        Text(
            text = text,
            style = textStyle,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}