package com.github.k409.fitflow.ui.screen.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.ui.common.thenIf

@Composable
fun LeaderboardCard(
    modifier: Modifier = Modifier,
    user: User,
    rank: Int,
    currentUser: Boolean,
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            text = rank.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (currentUser) {
                FontWeight.Bold
            } else FontWeight.Normal,
            color = colors.primary,
        )

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(user.photoUrl).crossfade(true)
                .build(),
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
                .size(38.dp)
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

        Spacer(modifier = Modifier.width(2.dp))

        Text(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .thenIf(currentUser) { background(colors.secondaryContainer) }
                .padding(horizontal = 8.dp, vertical = 3.dp),
            text = user.name.ifEmpty { user.email }.substringBefore("@"),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (currentUser) {
                FontWeight.Bold
            } else FontWeight.Normal,
            color = colors.primary,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            modifier = Modifier
                .padding(bottom = 3.dp)
                .size(18.dp),
            painter = painterResource(id = R.drawable.xp),
            contentDescription = null,
            tint = Color.Unspecified,
        )

        Text(
            text = "${user.xp}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (currentUser) {
                FontWeight.Bold
            } else FontWeight.Normal,
            color = colors.primary,
        )
    }
}
