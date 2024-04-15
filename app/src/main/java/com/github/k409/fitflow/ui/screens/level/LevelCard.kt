package com.github.k409.fitflow.ui.screens.level

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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
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
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = colors.primaryContainer,
                    shape = CardDefaults.shape,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Level badge",
                tint = Color.Unspecified,
                modifier = Modifier.size(80.dp)
                    .padding(start = 12.dp),
            )

            Column(modifier = modifier.padding(12.dp)) {
                Text(
                    text = "$id. $name",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                )
                if (userXp in minXp..maxXp) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Text(
                                text =
                                if (maxXp == Int.MAX_VALUE) {
                                    "$userXp / ..."
                                } else {
                                    "$userXp / $maxXp"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.primary,
                                modifier = Modifier.padding(end = 1.dp),
                            )
                            Spacer(Modifier.weight(1f))
                        }
                        LinearProgressIndicator(
                            progress = {
                                if (maxXp == Int.MAX_VALUE) { userXp.toFloat() / userXp.toFloat() } else { userXp.toFloat() / maxXp.toFloat() }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .padding(end = 8.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = colors.primary,
                        )
                    }
                } else if (maxXp == Int.MAX_VALUE) {
                    Text(
                        text = "$minXp++",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Normal,
                        color = colors.primary,
                        textAlign = TextAlign.Justify,
                    )
                } else {
                    Text(
                        text = "$minXp -> $maxXp",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Normal,
                        color = colors.primary,
                        textAlign = TextAlign.Justify,
                    )
                }
            }
        }
    }
}
