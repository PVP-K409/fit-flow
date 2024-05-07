package com.github.k409.fitflow.ui.screen.friendSearchInvite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firebase.ui.auth.data.model.User
import com.github.k409.fitflow.data.UserRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun FriendsScreen(
    friendsViewModel: FriendsViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search by email") },
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .padding(start = 16.dp, end = 0.dp),
        )

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 32.dp),
            onClick = {
                coroutineScope.launch {
                    friendsViewModel.sendFriendRequest(searchText)
                }
            },
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
            )
        }
    }
}
