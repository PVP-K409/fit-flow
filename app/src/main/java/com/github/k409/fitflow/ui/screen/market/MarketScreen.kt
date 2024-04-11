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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.ConfirmDialog
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.item.CategorySelectHeader
import com.github.k409.fitflow.ui.common.item.InventoryItemCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketScreen(
    marketViewModel: MarketViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }
    var addClicked by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(com.github.k409.fitflow.model.Item()) }

    val marketUiState by marketViewModel.marketUiState.collectAsStateWithLifecycle()

    if (marketUiState is MarketUiState.Loading) {
        FitFlowCircularProgressIndicator()
        return
    }

    val allItems = (marketUiState as MarketUiState.Success).items[0]

    //Log.d("MarketScreen", allItems.size.toString())
    //Log.d("MarketScreen", allItems[0].phases?.get("Regular") ?: "")

    val items = when (selectedCategoryIndex) {
        0 -> allItems.filter { it.type == "fish"} // Replace with fishes from db later
        1 -> allItems.filter { it.type == "decoration"} // Replace with decorations from db later
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
                imageUrl = item.phases?.get("Regular") ?: item.image,
                name = item.title,
                description = item.description,
                removeButtonText = "Sell for ${item.price / 2}",
                onRemoveClick =
                {
                    showDialog = true
                    addClicked = false
                    dialogText = "Are you sure you want to sell ${item.title}?"
                    selectedItem = item
                },
                addButtonText = "Buy for ${item.price}",
                onAddClick =
                {
                    showDialog = true
                    addClicked = true
                    dialogText = "Are you sure you want to buy ${item.title}?"
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
                //context = context,
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
                        "${selectedItem.title} has been added to your inventory",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "${selectedItem.title} has been sold",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}
