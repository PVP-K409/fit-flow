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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.service.SnackbarManager
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun FriendsScreen() {
    val selectedTabIndex = remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    Column {
        SecondaryTextTabsRow(
            titles = listOf(
                stringResource(R.string.FRIENDSLIST),
                stringResource(R.string.FRIENDREQUESTS),
            ),
            selectedTabIndex = selectedTabIndex,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 16.dp)
                .padding(bottom = 6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            when (selectedTabIndex.intValue) {
                0 -> {
                    FriendsListScreen()
                }

                1 -> {
                    FriendRequestScreen()
                }

            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SecondaryTextTabsRow(
    titles: List<String>,
    selectedTabIndex: MutableIntState = remember {
        mutableIntStateOf(0)
    },
) {
    SecondaryTabRow(selectedTabIndex = selectedTabIndex.intValue) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex.intValue == index,
                onClick = { selectedTabIndex.intValue = index },
                text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
            )
        }
    }
}
