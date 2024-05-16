package com.github.k409.fitflow.ui.screen.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.screen.friends.FriendsUiState
import com.github.k409.fitflow.ui.screen.friends.FriendsViewModel
import com.github.k409.fitflow.ui.screen.you.OutlineCardContainer
import kotlinx.coroutines.flow.Flow

@Composable
fun FriendsLeaderboard(
    viewModel: FriendsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.friendsUiState.collectAsState()

    when (uiState) {
        is FriendsUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }

        is FriendsUiState.Success -> {
            FriendsLeaderboardContent(
                viewModel = viewModel,
                uiState = uiState as FriendsUiState.Success)
        }
    }
}

@Composable
fun FriendsLeaderboardContent(
    viewModel: FriendsViewModel,
    uiState: FriendsUiState.Success) {

    val currentUser = viewModel.getCurrentUser().collectAsState(User()).value
    val friends = sortLeaderboard(uiState.friends, currentUser)

    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .padding(start = 2.dp, end = 2.dp),
    ) {
        OutlineCardContainer(
            title = stringResource(id = R.string.friends_leaderboard),
            subtitleText = stringResource(R.string.your_placings_among_your_friends),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                friends.forEachIndexed { index, friend ->
                    LeaderboardCard(
                        user = friend,
                        rank = friend.rank,
                        currentUser = currentUser.uid == friend.uid,
                    )

                    if (index != friends.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
@Composable
private fun sortLeaderboard(
    friends: List<Flow<User>>,
    currentUser: User,
): List<User> {
    val collectedFriends = friends.map { it.collectAsState(User()).value }
    val allUsers = collectedFriends + currentUser

    return allUsers.sortedByDescending { it.xp }
        .mapIndexed { index, user -> user.copy(rank = index + 1) }
}
