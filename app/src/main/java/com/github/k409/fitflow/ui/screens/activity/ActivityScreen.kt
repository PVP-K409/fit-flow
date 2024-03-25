package com.github.k409.fitflow.ui.screens.activity

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.ui.common.HorizontalPagerIndicator

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityScreen(
    activityViewModel: ActivityViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState(pageCount = {
        2
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
        ) { page ->
            when (page) {
                0 -> {
                    ActivityPage(activityViewModel = activityViewModel)
                }

                1 -> {
                    ProgressGraphPage(activityViewModel = activityViewModel)
                }
            }
        }

        HorizontalPagerIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            pagerState = pagerState,
            defaultRadius = 6.dp,
        )
    }
}
