package com.github.k409.fitflow.ui.screen.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator

@Composable
fun GlobalLeaderboardScreen(
    viewModel: LeaderboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.leaderboardUiState.collectAsState()

    when (uiState) {
        is LeaderboardUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }

        is LeaderboardUiState.Success -> {
            GlobalLeaderboardScreenContent(uiState = uiState as LeaderboardUiState.Success)
        }
    }
}

@Composable
fun GlobalLeaderboardScreenContent(uiState: LeaderboardUiState.Success) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(uiState.users.size) { index ->
            val user = uiState.users[index]

            LeaderboardCard(
                user = user,
                index = index + 1,
            )
        }
    }
}
