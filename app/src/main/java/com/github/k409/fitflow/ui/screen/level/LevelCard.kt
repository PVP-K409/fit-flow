package com.github.k409.fitflow.ui.screen.level

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.Level
import com.github.k409.fitflow.model.MarketItem
import com.github.k409.fitflow.model.getProgress
import com.github.k409.fitflow.model.getProgressText
import com.github.k409.fitflow.ui.common.Dialog
import com.github.k409.fitflow.ui.common.item.InventoryItemCardWithoutButtons

@Composable
fun LevelCard(
    modifier: Modifier = Modifier,
    level: Level,
    name: String,
    minXp: Int,
    maxXp: Int,
    userXp: Int,
    icon: Int,
    rewardItem: MarketItem? = null,
) {
    val colors = MaterialTheme.colorScheme
    val isDialogOpen = remember { mutableStateOf(false) }

    val progress = level.getProgress(userXp)
    val levelProgressText = level.getProgressText(userXp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Level badge",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(56.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                    )
                    if (rewardItem != null) {
                        Icon(
                            modifier = Modifier
                                .size(22.dp)
                                .clickable {
                                    isDialogOpen.value = true
                                },
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    if (maxXp == Int.MAX_VALUE) {
                        Text(
                            text = "$minXp +",
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
                            modifier = Modifier
                                .size(18.dp),
                        )

                        Text(
                            text = "$maxXp",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Normal,
                            color = colors.primary,
                        )
                    }
                }

                Column {
                    Text(
                        text = levelProgressText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Normal,
                        color = colors.primary,
                        modifier = Modifier.padding(end = 2.dp),
                    )

                    val progressColor =
                        if (progress >= 1) colors.primary else colors.primary.copy(alpha = 0.5f)

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 6.dp, top = 2.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = progressColor,
                    )
                }
            }
        }
    }
    if (isDialogOpen.value && rewardItem != null) {
        Dialog(
            title = stringResource(R.string.your_reward_for_reaching_level, name),
            onDismiss = { isDialogOpen.value = false },
            buttonsVisible = false,
            content = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(1) {
                        InventoryItemCardWithoutButtons(
                            modifier = Modifier,
                            imageDownloadUrl = rewardItem.phases?.get("Regular")
                                ?: rewardItem.image,
                            name = rewardItem.title,
                            description = rewardItem.description,
                        )
                    }
                }
            },
            onSaveClick = { isDialogOpen.value = false },
        )
    }
}
