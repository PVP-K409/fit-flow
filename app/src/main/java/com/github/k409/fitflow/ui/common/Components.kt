package com.github.k409.fitflow.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FitFlowCircularProgressIndicator(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 22.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(45.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainer,
            strokeWidth = 5.dp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FancyIndicatorTabs(
    values: List<String>,
    selectedIndex: Int,
    onValueChange: (Int) -> Unit,
) {
    Column {
        ElevatedCard {
            PrimaryTabRow(
                modifier = Modifier.clip(MaterialTheme.shapes.small),
                selectedTabIndex = selectedIndex,
                divider = {},
            ) {
                values.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = {
                            onValueChange(index)
                        },
                        text = { Text(title) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    defaultRadius: Dp = 8.dp,
    space: Dp = 6.dp,
    activeColor: Color = MaterialTheme.colorScheme.onSurface,
    inactiveColor: Color = activeColor.copy(alpha = 0.5f)
) {
    Row(
        modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color =
                if (pagerState.currentPage == iteration) activeColor else inactiveColor

            Box(
                modifier = Modifier
                    .padding(horizontal = space / 2)
                    .clip(CircleShape)
                    .background(color)
                    .size(defaultRadius)
            )
        }
    }
}
