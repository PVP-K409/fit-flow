package com.github.k409.fitflow.ui.screen.leaderboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
    val topFive = uiState.users.take(5)
    val otherUsers = uiState.users.drop(5)

    Box(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
        Text(
            text = "Top 5 users",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }

    for (index in topFive.indices) {
        val user = topFive[index]

        Box(modifier = Modifier.padding(top = if (index == 0) 0.dp else 16.dp)) {
            LeaderboardCard(
                user = user,
                rank = user.rank,
            )
        }
    }

    if (uiState.users.size > 5) {
        Box(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            Text(
                text = "Your placing",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }

    for (index in otherUsers.indices) {
        val user = otherUsers[index]

        Box(modifier = Modifier.padding(top = if (index == 0) 0.dp else 16.dp)) {
            LeaderboardCard(
                user = user,
                rank = user.rank,
            )
        }
    }
}
