package com.github.k409.fitflow.ui.common.item

import android.annotation.SuppressLint
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.screen.market.MarketViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
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
) {
    val colors = MaterialTheme.colorScheme
    val coroutineScope = rememberCoroutineScope()
    var imageDownloadUrl by remember { mutableStateOf("") }
    coroutineScope.launch {
        imageDownloadUrl = marketViewModel.getImageDownloadUrl(imageUrl)
    }

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
                        color = colors.tertiary,
                        shape = CardDefaults.shape,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (imageDownloadUrl.isNotEmpty()) {
                    //Log.d("ItemCard", "Composing $imageDownloadUrl")
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageDownloadUrl)
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        error = painterResource(R.drawable.error_24px),
                        contentDescription = name,
                        modifier = modifier
                            .padding(8.dp)
                            .size(80.dp),
                    )
                }

                Column(modifier = modifier.padding(8.dp)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onTertiary,
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Normal,
                        color = colors.onTertiary,
                        textAlign = TextAlign.Justify,
                    )
                }
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

