package com.github.k409.fitflow.ui.screen.goals

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
import androidx.navigation.NavHostController
import com.github.k409.fitflow.ui.common.HorizontalPagerIndicator

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoalsScreen(
    navController: NavHostController,
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
                    GoalsPage(navController = navController)
                }

                1 -> {
                    ExercisesLogPage()
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