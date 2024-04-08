package com.github.k409.fitflow.ui.screens.userLevel

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.model.User

@Composable
fun LevelCard(
    modifier: Modifier,
    name: String,
    minXp: Int,
    maxXp: Int,
) {
    val colors = MaterialTheme.colorScheme
    val user = User()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.tertiaryContainer, shape = CardDefaults.shape),
    ) {
        Column(
            modifier = modifier
                .padding(8.dp)
                .fillMaxSize(),
        ) {
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        color = colors.tertiary,
                        shape = CardDefaults.shape,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Column(modifier = modifier.padding(12.dp)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onTertiary,
                    )
                    if(maxXp == Int.MAX_VALUE){
                        Text(
                            text = "$minXp++",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Normal,
                            color = colors.onTertiary,
                            textAlign = TextAlign.Justify,
                        )
                    } else {
                        Text(
                            text = "$minXp -> $maxXp",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Normal,
                            color = colors.onTertiary,
                            textAlign = TextAlign.Justify,
                        )
                    }
                }

                if(user.xp in minXp..maxXp) {
                    ElevatedAssistChip(
                        border = AssistChipDefaults.assistChipBorder(enabled = false),
                        shape = RoundedCornerShape(100),
                        onClick = { },
                        label = {
                            Text(
                                text = "Current level",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        },
                    )
                }
            }
        }
    }
}