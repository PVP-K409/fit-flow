package com.github.k409.fitflow.ui.screen.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.service.SnackbarManager
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.screen.you.OutlineCardContainer
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun FriendRequestScreen (
    viewModel: FriendsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.friendsUiState.collectAsState()

    when (uiState) {
        is FriendsUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }
        is FriendsUiState.Success -> {
            FriendRequestContent(
                viewModel = viewModel,
                uiState = uiState as FriendsUiState.Success,
            )
        }
    }
}

@Composable
fun FriendRequestContent(
    viewModel: FriendsViewModel,
    uiState: FriendsUiState.Success,
) {
    val friendRequests = uiState.friendRequests
    val friends = uiState.friends

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(true) }
    var nameError by remember { mutableStateOf<String?>(null) }
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    var foundUser by remember { mutableStateOf<User?>(null) }

    val friendEmails = friends.map { it.collectAsState(User()).value.email }
    val requestEmails =  friendRequests.map { it.collectAsState(User()).value.email }

    val emails = friendEmails + requestEmails

    Column(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp, top = 4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    isButtonEnabled = !emails.contains(it.lowercase()) && it != userEmail
                    nameError = if (it == userEmail) context.getString(R.string.self_friend)
                    else if (emails.contains(it.lowercase())) context.getString(R.string.already_friends)
                    else null
                },
                label = { Text("Send a friend request to") },
                modifier = Modifier
                    .padding(start = 18.dp, end = 12.dp),
            )

            IconButton(
                modifier = Modifier
                    .padding(18.dp),
                onClick = {
                    coroutineScope.launch {
                        foundUser = viewModel.searchUser(searchText.lowercase())
                        if (foundUser?.uid == "") {
                            SnackbarManager.showMessage(context.getString(R.string.user_not_found))
                        }
                    }
                },
                enabled = isButtonEnabled,
            ) {
                Icon(
                    modifier = Modifier
                        .size(28.dp),
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                )
            }
        }
        nameError?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .padding(end = 2.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        OutlineCardContainer(
            title = stringResource(R.string.found_user),
            subtitleText = stringResource(R.string.display_searched_user),
        ) {
            if(foundUser?.uid != null && foundUser?.uid != "") {
                UserCard(
                    user = foundUser,
                    friendsViewModel = viewModel,
                    coroutineScope = coroutineScope,
                    context = context,
                )
            } else {
                Text(
                    text = stringResource(R.string.search_for_an_user),
                    modifier = Modifier
                        .alpha(0.5f)
                        .padding(16.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlineCardContainer(
            title = stringResource(R.string.received_friend_requests),
            subtitleText = stringResource(R.string.other_users_who_want_to_be_your_friend),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                friendRequests.forEach { friendRequest ->
                    friendRequest.collectAsState(User()).value.let { user ->
                        FriendRequestCard(
                            user = user,
                            friendsViewModel = viewModel,
                            coroutineScope = coroutineScope,
                            context = context,
                        )
                    }
                }
            }
        }
    }
}
