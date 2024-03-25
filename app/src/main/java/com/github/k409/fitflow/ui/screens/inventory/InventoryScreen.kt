@file:OptIn(ExperimentalFoundationApi::class)

package com.github.k409.fitflow.ui.screens.inventory

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R

@Composable
fun InventoryScreen() {
    val context = LocalContext.current

    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }

    val items = when (selectedCategoryIndex) {
        0 -> mockFishes
        1 -> mockDecorations
        else -> emptyList()
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        stickyHeader {
            CategorySelectHeader(
                selectedItemIndex = selectedCategoryIndex,
                items = categories,
                onItemSelected = { selectedCategoryIndex = it },
            )
        }
        items(items) { item ->
            InventoryItemCard(
                modifier = Modifier,
                painter = item.imageResId,
                name = item.name,
                description = item.description,
                onRemoveClick =
                {
                    Toast.makeText(
                        context,
                        "Removed from aquarium",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                onAddClick =
                {
                    Toast.makeText(
                        context,
                        "Added to aquarium",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectHeader(
    selectedItemIndex: Int,
    items: List<InventoryCategory>,
    onItemSelected: (Int) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        items.forEachIndexed { index, item ->
            SegmentedButton(
                icon = {},
                selected = selectedItemIndex == index,
                onClick = {
                    onItemSelected(index)
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, items.size),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = if (index == selectedItemIndex) {
                            item.selectedIcon
                        } else item.unselectedIcon,
                        contentDescription = item.title,
                    )
                    Text(
                        text = item.title,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    modifier: Modifier,
    painter: Int,
    name: String,
    description: String,
    onAddClick: () -> Unit = {},
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
                        Text(text = "Add")
                    }

                    Button(
                        onClick = onRemoveClick,
                        colors = ButtonDefaults.buttonColors(colors.error),
                    ) {
                        Text(text = "Remove")
                    }
                }
            }
        }
    }
}
