package com.github.k409.fitflow.ui.screen.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.screen.you.OutlineCardContainer

@Composable
fun FriendsListScreen(
    viewModel: FriendsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.friendsUiState.collectAsState()

    when (uiState) {
        is FriendsUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }
        is FriendsUiState.Success -> {
            FriendsListContent(
                viewModel = viewModel,
                uiState = uiState as FriendsUiState.Success,
            )
        }
    }
}

@Composable
fun FriendsListContent(
    viewModel: FriendsViewModel,
    uiState: FriendsUiState.Success,
) {
    val friends = uiState.friends

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .padding(start = 2.dp, end = 2.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        OutlineCardContainer(
            title = stringResource(id = R.string.friends),
            subtitleText = stringResource(R.string.people_you_are_friends_with),
        ) {
            if (friends.isEmpty()) {
                Text(
                    text = stringResource(R.string.you_don_t_have_any_friends_yet),
                    modifier = Modifier.padding(16.dp),
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    friends.forEach { friend ->
                        friend.collectAsState(User()).value.let { user ->
                            FriendCard(
                                user = user,
                                coroutineScope = coroutineScope,
                                friendsViewModel = viewModel,
                                context = context,
                            )
                        }
                    }
                }
            }
        }
    }
}