package com.github.k409.fitflow.ui.screen.market

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.ConfirmDialog
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.item.CategorySelectHeader
import com.github.k409.fitflow.ui.common.item.InventoryItemCard

@SuppressLint("CoroutineCreationDuringComposition")
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

    val allItems = (marketUiState as MarketUiState.Success).items
    val ownedItems = (marketUiState as MarketUiState.Success).ownedItems
    val user = (marketUiState as MarketUiState.Success).user

    val items = when (selectedCategoryIndex) {
        0 -> allItems.filter { it.type == "fish" }
        1 -> allItems.filter { it.type == "decoration" }
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
                removeButtonEnabled = ownedItems.find { it.id == item.id } != null,
                addButtonText = "Buy for ${item.price}",
                onAddClick =
                {
                    showDialog = true
                    addClicked = true
                    dialogText = "Are you sure you want to buy ${item.title}?"
                    selectedItem = item
                },
                addButtonEnabled = user.points >= item.price && ownedItems.find { it.id == item.id } == null,
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
            dialogTitle = stringResource(R.string.are_you_sure),
            dialogText = dialogText,
            onConfirm = {
                if (addClicked) {
                    marketViewModel.updateUserCoinBalance((-selectedItem.price).toLong())
                    marketViewModel.addItemToUserInventory(selectedItem)
                    Toast.makeText(
                        context,
                        "${selectedItem.title} has been added to your inventory",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    marketViewModel.updateUserCoinBalance((selectedItem.price / 2).toLong())
                    marketViewModel.removeItemFromUserInventory(selectedItem)
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
