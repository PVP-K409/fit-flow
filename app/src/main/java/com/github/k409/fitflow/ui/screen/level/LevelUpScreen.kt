package com.github.k409.fitflow.ui.screen.level

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.levels
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.item.InventoryItemCardWithoutButtons
import kotlinx.coroutines.launch

@Composable
fun LevelUpScreen(
    viewModel: LevelViewModel = hiltViewModel(),
) {
    val uiState by viewModel.levelUiState.collectAsState()

    when (uiState) {
        is LevelUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }

        is LevelUiState.Success -> {
            LevelUpScreenContent(viewModel = viewModel, uiState = uiState as LevelUiState.Success)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun LevelUpScreenContent(viewModel: LevelViewModel, uiState: LevelUiState.Success) {
    val clicked = remember { mutableStateOf(false) }
    val level = levels.first { it.minXP <= uiState.user.xp && it.maxXP >= uiState.user.xp }
    val coroutineScope = rememberCoroutineScope()

    // unlike market items, reward items have document numeration starting from 1000
    val reward = uiState.rewards.firstOrNull { it.id == level.id + 1000 }

    val colors = MaterialTheme.colorScheme
    val background = Brush.linearGradient(
        colors = listOf(
            colors.tertiaryContainer,
            colors.onTertiaryContainer,
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.congratulations_you_have_leveled_up_to, level.name),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
            )
            Icon(
                painter = painterResource(id = level.icon),
                contentDescription = "Level badge",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(120.dp),
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = stringResource(R.string.you_have_unlocked_the_following_reward),
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.padding(4.dp))
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(1) {
                    if (reward != null) {
                        InventoryItemCardWithoutButtons(
                            modifier = Modifier,
                            imageDownloadUrl = reward.phases?.get("Regular") ?: reward.image,
                            name = reward.title,
                            description = reward.description,
                        )
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            clicked.value = true
                        },
                    ) {
                        Text(
                            text = stringResource(R.string.awesome),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
    if (clicked.value) {
        clicked.value = false

        coroutineScope.launch {
            // Log.d("LevelUpScreenContent", "Adding reward item to user inventory")
            viewModel.updateUserField("hasLeveledUp", false)
            viewModel.addRewardItemToUserInventory(level.id)
        }
    }
}
