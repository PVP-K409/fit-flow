package com.github.k409.fitflow.ui.common.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Euro
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.noRippleClickable
import com.github.k409.fitflow.ui.screen.checkout.PaymentUiState

@Composable
fun InventoryItemCard(
    modifier: Modifier,
    imageDownloadUrl: String,
    name: String,
    description: String,
    addButtonText: String,
    onAddClick: () -> Unit = {},
    addButtonEnabled: Boolean,
    removeButtonText: String,
    onRemoveClick: () -> Unit = {},
    removeButtonEnabled: Boolean,
    coinIcon: @Composable () -> Unit,
) {
    val colors = colorScheme

    InventoryItemCardWrapper(
        modifier = modifier,
        imageDownloadUrl = imageDownloadUrl,
        name = name,
        description = description,
    ) {
        Row(
            modifier = modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Button(
                enabled = removeButtonEnabled,
                onClick = onRemoveClick,
                colors = ButtonDefaults.buttonColors(colors.error),
            ) {
                Text(text = removeButtonText)
                coinIcon()
            }

            Button(
                enabled = addButtonEnabled,
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(colors.primary),
            ) {
                Text(text = addButtonText)
                coinIcon()
            }
        }
    }
}

@Composable
fun InventoryItemCardWithoutButtons(
    modifier: Modifier,
    imageDownloadUrl: String,
    name: String,
    description: String,
) {
    InventoryItemCardWrapper(
        modifier = modifier,
        imageDownloadUrl = imageDownloadUrl,
        name = name,
        description = description,
        expandedOnly = true,
    ) {
    }
}

@Composable
fun InventoryItemCardGooglePay(
    modifier: Modifier,
    imageDownloadUrl: String,
    name: String,
    description: String,
    owned: Boolean,
    priceCents: Long,
    payUiState: PaymentUiState,
    onGooglePayButtonClick: () -> Unit,
) {
    InventoryItemCardWrapper(
        modifier = modifier,
        imageDownloadUrl = imageDownloadUrl,
        name = name,
        description = description,
    ) {
        Row(
            modifier = modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val price = (priceCents / 100.0)

            if (payUiState !is PaymentUiState.NotStarted) {
                EuroPriceCard(
                    modifier = modifier,
                    colorScheme = colorScheme,
                    price = price,
                )

                GooglePayButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onGooglePayButtonClick() },
                    enabledPaying = payUiState !is PaymentUiState.Error && !owned,
                    showGoogleIcon = !owned,
                    disabledMessage = if (payUiState is PaymentUiState.Error) {
                        stringResource(R.string.unavailable)
                    } else {
                        stringResource(
                            R.string.in_inventory,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun EuroPriceCard(
    modifier: Modifier,
    colorScheme: ColorScheme,
    price: Double,
) {
    Row(
        modifier = modifier
            .defaultMinSize(
                minWidth = ButtonDefaults.MinWidth,
                minHeight = ButtonDefaults.MinHeight,
            )
            .clip(ButtonDefaults.filledTonalShape)
            .background(colorScheme.onTertiary)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "$price", style = MaterialTheme.typography.labelLarge)

        Icon(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(20.dp),
            imageVector = Icons.Outlined.Euro,
            contentDescription = "",
        )
    }
}

@Composable
private fun GooglePayButton(
    modifier: Modifier = Modifier,
    enabledPaying: Boolean = true,
    showGoogleIcon: Boolean = true,
    disabledMessage: String,
    onClick: () -> Unit,
) {
    ElevatedButton(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing,
                ),
            ),
        onClick = onClick,
        enabled = enabledPaying,
        shape = MaterialTheme.shapes.extraLarge,
        border = if (enabledPaying) ButtonDefaults.outlinedButtonBorder else null,
    ) {
        if (showGoogleIcon) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if (enabledPaying) stringResource(R.string.pay) else disabledMessage,
            color = if (enabledPaying) colorScheme.onSurface else Color.Unspecified,
        )
    }
}

@Composable
private fun InventoryItemCardWrapper(
    modifier: Modifier,
    imageDownloadUrl: String,
    name: String,
    description: String,
    expandedOnly: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = colorScheme

    var expandedState by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f,
        label = "",
    )

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageDownloadUrl)
            .size(Size.ORIGINAL)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(CardDefaults.shape)
            .background(color = colors.tertiaryContainer, shape = CardDefaults.shape)
            .noRippleClickable(onClick = { expandedState = !expandedState }),
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        color = colors.onTertiary,
                        shape = CardDefaults.shape,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Image(
                    painter = painter,
                    contentDescription = name,
                    modifier = modifier
                        .padding(8.dp)
                        .size(80.dp),
                )

                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.tertiary,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    if (!expandedOnly) {
                        IconButton(
                            modifier = Modifier
                                .rotate(rotationState),
                            onClick = {
                                expandedState = !expandedState
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = expandedState || expandedOnly) {
                Column(modifier = modifier.padding(8.dp)) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Light,
                        color = colors.onTertiaryContainer,
                        textAlign = TextAlign.Justify,
                    )
                }
            }

            Spacer(modifier = modifier.height(16.dp))

            content()
        }
    }
}
