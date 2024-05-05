package com.github.k409.fitflow.ui.screen.level

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.model.levels
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator

@Composable
fun LevelScreen(
    viewModel: LevelUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.levelUpUiState.collectAsState()

    when (uiState) {
        is LevelUpUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }

        is LevelUpUiState.Success -> {
            LevelScreenContent(uiState = uiState as LevelUpUiState.Success)
        }
    }
}

@Composable
private fun LevelScreenContent(uiState: LevelUpUiState.Success) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(levels.size) { index ->
            val level = levels[index]

            LevelCard(
                name = level.name,
                minXp = level.minXP,
                maxXp = level.maxXP,
                userXp = uiState.user.xp,
                icon = level.icon,
                rewardItem = uiState.rewards.firstOrNull { it.id == level.id + 1000 },
            )
        }
    }
}
