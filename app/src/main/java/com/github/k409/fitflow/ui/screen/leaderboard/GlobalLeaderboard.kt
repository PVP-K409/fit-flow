package com.github.k409.fitflow.ui.screen.leaderboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
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

    Box(modifier = Modifier.padding(top = 22.dp, bottom = 22.dp)) {
        Text(
            text = "Top 5 users",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }

    for (index in topFive.indices) {
        val user = topFive[index]

        Box(modifier = Modifier.padding(top = if (index == 0) 0.dp else 22.dp)) {
            LeaderboardCard(
                user = user,
                rank = user.rank,
            )
        }
    }

    Box(modifier = Modifier.padding(top = 22.dp, bottom = 22.dp)) {
        HorizontalDivider(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            color = MaterialTheme.colorScheme.primary,
            thickness = 5.dp,
        )
    }

    for (index in otherUsers.indices) {
        val user = otherUsers[index]

        Box(modifier = Modifier.padding(top = if (index == 0) 0.dp else 22.dp)) {
            LeaderboardCard(
                user = user,
                rank = user.rank,
            )
        }
    }
}
