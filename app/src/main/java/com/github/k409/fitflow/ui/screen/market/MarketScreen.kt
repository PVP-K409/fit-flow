package com.github.k409.fitflow.ui.screen.market

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.ConfirmDialog
import com.github.k409.fitflow.ui.common.item.CategorySelectHeader
import com.github.k409.fitflow.ui.common.item.InventoryItemCard
import com.github.k409.fitflow.ui.common.item.Item
import com.github.k409.fitflow.ui.common.item.mockDecorations
import com.github.k409.fitflow.ui.common.item.mockFishes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketScreen() {
    val context = LocalContext.current

    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }
    var addClicked by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(Item()) }

    val items = when (selectedCategoryIndex) {
        0 -> mockFishes // Replace with fishes from db later
        1 -> mockDecorations // Replace with decorations from db later
        else -> emptyList()
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        stickyHeader {
            CategorySelectHeader(
                selectedItemIndex = selectedCategoryIndex,
                onItemSelected = { selectedCategoryIndex = it },
            )
        }
        items(items) { item ->
            InventoryItemCard(
                modifier = Modifier,
                painter = item.imageResId,
                name = item.name,
                description = item.description,
                removeButtonText = "Sell for 5",
                onRemoveClick =
                {
                    showDialog = true
                    addClicked = false
                    dialogText = "Are you sure you want to sell " + item.name + "?"
                    selectedItem = item
                },
                addButtonText = "Buy for 10",
                onAddClick =
                {
                    showDialog = true
                    addClicked = true
                    dialogText = "Are you sure you want to buy " + item.name + "?"
                    selectedItem = item
                },
                coinIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(20.dp),
                        painter = painterResource(id = R.drawable.coin),
                        tint = Color.Unspecified,
                        contentDescription = "",
                    )
                },
            )
        }
    }
    if (showDialog) {
        ConfirmDialog(
            dialogTitle = "Are you sure?",
            dialogText = dialogText,
            onConfirm = {
                if (addClicked) {
                    Toast.makeText(
                        context,
                        selectedItem.name + " has been added to your inventory",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        selectedItem.name + " has been sold",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}
