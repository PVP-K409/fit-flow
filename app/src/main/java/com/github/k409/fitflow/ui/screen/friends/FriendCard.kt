package com.github.k409.fitflow.ui.screen.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.service.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Card to show friend request
@Composable
fun FriendRequestCard(
    user: User,
    coroutineScope: CoroutineScope,
    friendsViewModel: FriendsViewModel,
    context: android.content.Context,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(
                    LocalContext.current,
                ).data(user.photoUrl).crossfade(true)
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
                    .size(80.dp)
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

            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .padding(4.dp),
                    text = user.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .padding(4.dp),
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Button(
                        modifier = Modifier
                            .padding(start = 6.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        onClick = {
                            coroutineScope.launch {
                                friendsViewModel.acceptFriendRequest(user.uid)
                            }
                            SnackbarManager.showMessage(
                                context.getString(R.string.friend_request_accepted),
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                        )
                    }

                    Button(
                        modifier = Modifier.padding(start = 6.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                        onClick = {
                            coroutineScope.launch {
                                friendsViewModel.declineFriendRequest(user.uid)
                            }
                            SnackbarManager.showMessage(
                                context.getString(R.string.friend_request_declined),
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

// Card to show user that the current user searched for
@Composable
fun UserCard(
    user: User?,
    friendsViewModel: FriendsViewModel,
    coroutineScope: CoroutineScope,
    context: android.content.Context,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(
                    LocalContext.current,
                ).data(user?.photoUrl).crossfade(true)
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
                    .size(80.dp)
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

            Column {
                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .padding(4.dp),
                    text = user?.name.toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .padding(4.dp),
                    text = user?.email.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        friendsViewModel.sendFriendRequest(user?.uid.toString())
                    }
                    SnackbarManager.showMessage(context.getString(R.string.friend_request_sent))
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAddAlt1,
                    contentDescription = null,
                )
            }
        }
    }
}

// Card to show a friend
@Composable
fun FriendCard(
    user: User,
    coroutineScope: CoroutineScope,
    friendsViewModel: FriendsViewModel,
    context: android.content.Context,
    onRemoveClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(user.photoUrl)
                    .crossfade(true)
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
                    .size(80.dp)
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

            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .padding(4.dp),
                    text = user.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .padding(4.dp),
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Button(
                    modifier = Modifier
                        .padding(start = 6.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                    onClick = onRemoveClick,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOff,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}
