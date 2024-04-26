package com.github.k409.fitflow.ui.screen.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.screen.you.OutlineCardContainer
import com.google.firebase.auth.FirebaseAuth

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

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlineCardContainer(
            title = stringResource(id = R.string.top_5_users),
            subtitleText = stringResource(R.string.top_5_users_in_the_global_leaderboard),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                topFive.indices.forEach { index ->
                    val user = topFive[index]

                    LeaderboardCard(
                        user = user,
                        rank = user.rank,
                        // TODO: remove this later
                        currentUser = FirebaseAuth.getInstance().currentUser!!.uid == user.uid
                    )

                    if (index != topFive.size - 1) {
                        HorizontalDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlineCardContainer(
            title = stringResource(R.string.other_users),
            subtitleText = stringResource(R.string.other_users_in_the_global_leaderboard),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                otherUsers.indices.forEach { index ->
                    val user = otherUsers[index]

                    LeaderboardCard(
                        user = user,
                        rank = user.rank,
                        // TODO: remove this later
                        currentUser = FirebaseAuth.getInstance().currentUser!!.uid == user.uid
                    )

                    if (index != otherUsers.size - 1) {
                        HorizontalDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
