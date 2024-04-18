package com.github.k409.fitflow.ui.common.item

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.github.k409.fitflow.ui.screen.market.MarketViewModel

@SuppressLint("RememberReturnType")
@Composable
fun InventoryItemCard(
    modifier: Modifier,
    imageUrl: String,
    name: String,
    description: String,
    addButtonText: String,
    onAddClick: () -> Unit = {},
    addButtonEnabled: Boolean,
    removeButtonText: String,
    onRemoveClick: () -> Unit = {},
    removeButtonEnabled: Boolean,
    coinIcon: @Composable () -> Unit,
    marketViewModel: MarketViewModel = hiltViewModel(),
    selectedCategoryIndex: Int,
) {
    val colors = MaterialTheme.colorScheme
    var imageDownloadUrl by rememberSaveable { mutableStateOf("") }

    // Trigger image recomposition when selected item category changes
    LaunchedEffect(key1 = selectedCategoryIndex) {
        // if (imageDownloadUrl.isEmpty())
        imageDownloadUrl = marketViewModel.getImageDownloadUrl(imageUrl)
    }
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
            .background(color = colors.tertiaryContainer, shape = CardDefaults.shape),
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
                // if (imageDownloadUrl.isNotEmpty()) {
                // Log.d("ItemCard", "Composing $name")
                // Log.d("ItemCard", "Composing $imageDownloadUrl")
                Image(
                    painter = painter,
                    contentDescription = name,
                    modifier = modifier
                        .padding(8.dp)
                        .size(80.dp),
                )
                // }
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.tertiary,
                )
            }
            Column(modifier = modifier.padding(8.dp)) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,

                    fontWeight = FontWeight.Light,
                    color = colors.onTertiaryContainer,
                    textAlign = TextAlign.Justify,
                )
            }

            Spacer(modifier = modifier.height(16.dp))

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
}
