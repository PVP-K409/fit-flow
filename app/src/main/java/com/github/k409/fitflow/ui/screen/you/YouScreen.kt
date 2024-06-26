package com.github.k409.fitflow.ui.screen.you

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.screen.leaderboard.FriendsLeaderboard
import com.github.k409.fitflow.ui.screen.leaderboard.GlobalLeaderboardScreen

@Composable
fun YouScreen(
    youViewModel: YouViewModel = hiltViewModel(),
) {
    val youUiState by youViewModel.youUiState.collectAsStateWithLifecycle()

    val selectedTabIndex = remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    if (youUiState is YouUiState.Loading) {
        FitFlowCircularProgressIndicator()
        return
    }

    Column {
        SecondaryTextTabsRow(
            titles = listOf(
                stringResource(R.string.progress_tab_title),
                stringResource(R.string.global_leaderboard_tab_title),
                stringResource(R.string.friends_leaderboard_tab_title),
            ),
            selectedTabIndex = selectedTabIndex,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 16.dp)
                .padding(bottom = 6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            when (selectedTabIndex.intValue) {
                0 -> {
                    ProgressGraphPage(youViewModel)
                }

                1 -> {
                    GlobalLeaderboardScreen()
                }

                2 -> {
                    FriendsLeaderboard()
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
    SecondaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex.intValue,
        edgePadding = 0.dp,
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex.intValue == index,
                onClick = { selectedTabIndex.intValue = index },
                text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
            )
        }
    }
}
