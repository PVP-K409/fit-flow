package com.github.k409.fitflow.ui.screen.info

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.util.annotatedStringResource

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
    ) {
        ElevatedCardContainer(
            title = stringResource(R.string.app_name),
            description = R.string.info_app_description,
        )

        ElevatedCardContainer(
            title = stringResource(R.string.app_usage),
            description = R.string.info_app_usage,
        )

        ElevatedCardContainer(
            title = stringResource(R.string.health_connect),
            description = R.string.info_health_connect,
        )
    }
}

@Composable
private fun ElevatedCardContainer(
    modifier: Modifier = Modifier,
    title: String,
    @StringRes description: Int? = null,
    content: @Composable () -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier
            .padding(16.dp),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                modifier = Modifier.padding(bottom = 6.dp),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )

            description?.let {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = annotatedStringResource(id = it),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Justify,
                )
            }

            content()
        }
    }
}
