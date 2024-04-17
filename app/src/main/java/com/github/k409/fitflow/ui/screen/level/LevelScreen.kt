package com.github.k409.fitflow.ui.screen.level

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator

@Composable
fun LevelScreen(
    viewModel: LevelViewModel = hiltViewModel(),
) {
    val uiState by viewModel.levelUiState.collectAsState()

    when (uiState) {
        is LevelUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }

        is LevelUiState.Success -> {
            LevelScreenContent(uiState = uiState as LevelUiState.Success)
        }
    }
}

@Composable
private fun LevelScreenContent(uiState: LevelUiState.Success) {
    LazyColumn {
        items(levels.size) { index ->
            val level = levels[index]
            LevelCard(
                modifier = Modifier.padding(8.dp),
                id = level.id,
                name = level.name,
                minXp = level.minXP,
                maxXp = level.maxXP,
                userXp = uiState.user.xp,
                icon = level.icon,
            )
        }
    }
}