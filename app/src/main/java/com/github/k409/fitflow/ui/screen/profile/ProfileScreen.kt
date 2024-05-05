package com.github.k409.fitflow.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.levels
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.navigation.NavRoutes

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onNavigate: (NavRoutes) -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val currentUser = profileViewModel.currentUser.collectAsState(initial = null).value

    if (currentUser == null) {
        FitFlowCircularProgressIndicator()

        return
    }

    val level = levels.find { it.minXP <= currentUser.xp && it.maxXP > currentUser.xp }
        ?: levels.first()
    val userXp = currentUser.xp
    val minXp = level.minXP
    val maxXp = level.maxXP
    val progress = (userXp - minXp) / (maxXp - minXp).toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(currentUser.photoUrl)
                    .crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = {
                    Icon(
                        modifier = Modifier.padding(3.dp),
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = null,
                    )
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(69.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape,
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        shape = CircleShape,
                    ),
            )

            ElevatedButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { onNavigate(NavRoutes.ProfileCreation) }) {
                Text(text = stringResource(R.string.edit_profile))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(span = {
                GridItemSpan(maxLineSpan)
            }) {
                WidgetCard(title = stringResource(id = R.string.email), value = currentUser.email)
            }

            item {
                WidgetCard(title = stringResource(R.string.level), value = level.name)
            }

            item {
                WidgetCard(
                    title = stringResource(R.string.xp),
                    value = currentUser.xp.toString()
                ) {
                    Icon(
                        modifier = Modifier
                            .size(18.dp)
                            .clickable {
                                onNavigate(NavRoutes.Levels)
                            },
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                    )
                }
            }

            item(span = {
                GridItemSpan(maxLineSpan)
            }) {

                WidgetCard(title = stringResource(R.string.level_progress)) {
                    Text(
                        text = if (maxXp == Int.MAX_VALUE) {
                            "$minXp+"
                        } else if (progress >= 1) {
                            "$maxXp / $maxXp"
                        } else {
                            "$userXp / $maxXp"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(end = 2.dp),
                    )

                    val progressColor =
                        if (progress >= 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.5f
                        )

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 6.dp, top = 2.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = progressColor,
                    )
                }
            }

            item {
                WidgetCard(
                    title = stringResource(id = R.string.points),
                    value = currentUser.points.toString()
                )
            }

            item {
                WidgetCard(
                    title = stringResource(R.string.height_cm),
                    value = currentUser.height.toString()
                )
            }

            item {
                WidgetCard(
                    title = stringResource(R.string.weight_kg),
                    value = currentUser.weight.toString()
                )
            }

            item {
                WidgetCard(title = stringResource(R.string.gender), value = currentUser.gender)
            }

            item {
                WidgetCard(
                    title = stringResource(R.string.birth_date),
                    value = currentUser.dateOfBirth
                )
            }
        }
    }
}

@Composable
private fun WidgetCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    actions: @Composable RowScope.() -> Unit = {},
) {
    WidgetCard(
        modifier = modifier,
        title = title,
        actions = actions,
    ) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
        )
    }
}

@Composable
private fun WidgetCard(
    modifier: Modifier = Modifier,
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                actions()
            }


            Spacer(modifier = Modifier.height(3.dp))

            content()
        }
    }
}