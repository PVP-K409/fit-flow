package com.github.k409.fitflow.ui.screen.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.friendsUiState.collectAsState()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email

    val scrollState = rememberScrollState()

    if (uiState is FriendsUiState.Loading) {
            FitFlowCircularProgressIndicator()
        return
    }

    val friendRequests = (uiState as FriendsUiState.Success).friendRequests
    val friends = (uiState as FriendsUiState.Success).friends

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(true) }
    var nameError by remember { mutableStateOf<String?>(null) }


    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(vertical = 16.dp)
            .padding(start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    isButtonEnabled = it != userEmail
                    nameError = if (it == userEmail) context.getString(R.string.self_friend) else null
                },
                label = { Text("Send a friend request to") },
                modifier = Modifier
                    .padding(start = 16.dp, end = 12.dp),
            )
            nameError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }


            IconButton(
                modifier = Modifier
                    .padding(18.dp),
                onClick = {
                    coroutineScope.launch {
                        viewModel.sendFriendRequest(searchText)
                    }
                },
                enabled = isButtonEnabled,
            ) {
                Icon(
                    modifier = Modifier
                        .size(28.dp),
                    imageVector = Icons.Outlined.PersonAddAlt1,
                    contentDescription = null,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        friendRequests.indices.forEach { index ->
            val user = friendRequests[index]

            FriendCard(
                user = user,
                coroutineScope = coroutineScope,
                friendsViewModel = viewModel,
                friendRequest = true
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        HorizontalDivider(
            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
            thickness = 8.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        friends.indices.forEach { index ->
            val user = friends[index]

            FriendCard(
                user = user,
                coroutineScope = coroutineScope,
                friendsViewModel = viewModel,
                friendRequest = false
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
