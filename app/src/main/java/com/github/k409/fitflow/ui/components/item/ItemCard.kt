package com.github.k409.fitflow.ui.components.item

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R

@Composable
fun InventoryItemCard(
    modifier: Modifier,
    painter: Int,
    name: String,
    description: String,
    addButtonText: String,
    onAddClick: () -> Unit = {},
    removeButtonText: String,
    onRemoveClick: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme

    Box {
        Image(
            modifier = modifier.matchParentSize(),
            painter = painterResource(id = R.drawable.chest3),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )

        Box(
            modifier = modifier
                .fillMaxSize(),
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
                            color = Color.White.copy(alpha = 0.3f),
                            shape = CardDefaults.shape,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Image(
                        modifier = modifier
                            .padding(8.dp)
                            .size(80.dp),
                        painter = painterResource(id = painter),
                        contentDescription = name,
                    )

                    Column(modifier = modifier.padding(8.dp)) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
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
                        onClick = onAddClick,
                    ) {
                        Text(text = addButtonText)
                    }

                    Button(
                        onClick = onRemoveClick,
                        colors = ButtonDefaults.buttonColors(colors.error),
                    ) {
                        Text(text = removeButtonText)
                    }
                }
            }
        }
    }
}