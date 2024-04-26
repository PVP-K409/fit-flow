package com.github.k409.fitflow.ui.screen.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.screen.you.SectionHeaderCard

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

    SectionHeaderCard(
        modifier = Modifier.padding(bottom = 16.dp),
        title = stringResource(R.string.top_5_users),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        for (index in topFive.indices) {
            val user = topFive[index]

            LeaderboardCard(
                user = user,
                rank = user.rank,
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary,
            thickness = 1.dp,
        )

        for (index in otherUsers.indices) {
            val user = otherUsers[index]

            LeaderboardCard(
                user = user,
                rank = user.rank,
            )
        }
    }
}
