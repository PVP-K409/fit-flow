package com.github.k409.fitflow.ui.screen.level

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LevelCard(
    modifier: Modifier,
    id: Int,
    name: String,
    minXp: Int,
    maxXp: Int,
    userXp: Int,
    icon: Int,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.primaryContainer)
            .padding(4.dp),
    ) {
        Row(
            modifier = modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Level badge",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(58.dp),
            )
            Column(
                modifier = modifier
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "$id. $name",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (maxXp == Int.MAX_VALUE) {
                        Text(
                            text = "$minXp+",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Normal,
                            color = colors.primary,
                        )
                    } else {
                        Text(
                            text = "$minXp",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Normal,
                            color = colors.primary,
                        )

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp),
                        )

                        Text(
                            text = "$maxXp",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Normal,
                            color = colors.primary,
                        )
                    }
                }

                val progress = if (maxXp == Int.MAX_VALUE) {
                    userXp.toFloat() / 10000
                } else {
                    userXp.toFloat() / maxXp
                }

                Column {
                    Text(
                        text =
                        if (maxXp == Int.MAX_VALUE) {
                            "$minXp+"
                        } else if (progress >= 1) {
                            "$maxXp/$maxXp"
                        } else {
                            "$userXp/$maxXp"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Normal,
                        color = colors.primary,
                        modifier = Modifier.padding(start = 8.dp, end = 2.dp),
                    )

                    val progressColor = if (progress >= 1) colors.primary else colors.primary.copy(alpha = 0.5f)

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 6.dp, start = 8.dp, top = 2.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = progressColor,
                    )
                }
            }
        }
    }
}
