package com.github.k409.fitflow.ui.screen.hydration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.HorizontalPagerIndicator
import com.github.k409.fitflow.ui.common.LocalSnackbarHostState
import com.github.k409.fitflow.ui.common.showSnackbarIfNotVisible
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HydrationScreen(
    viewModel: HydrationViewModel = hiltViewModel(),
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
                    HydrationMainPage(
                        viewModel = viewModel
                    )
                }

                1 -> {
                    HydrationLogsPage(
                        viewModel = viewModel
                    )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrinkButton(
    modifier: Modifier = Modifier,
    cupSize: Int,
    onDrink: () -> Unit = {},
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    Row(
        modifier = modifier
            .defaultMinSize(
                minWidth = ButtonDefaults.MinWidth,
                minHeight = ButtonDefaults.MinHeight,
            )
            .clip(ButtonDefaults.filledTonalShape)
            .combinedClickable(
                onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbarIfNotVisible(
                            message = context.getString(R.string.hold_drink_button_message),
                            withDismissAction = true,
                        )
                    }
                },
                onLongClick = {
                    onDrink()
                },
            )
            .background(colorScheme.secondaryContainer)
            .border(BorderStroke(1.0.dp, colorScheme.outline), CircleShape)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.drink_ml, cupSize),
            color = colorScheme.onSecondaryContainer,
            style = LocalTextStyle.current.merge(MaterialTheme.typography.labelLarge),
        )
    }
}
