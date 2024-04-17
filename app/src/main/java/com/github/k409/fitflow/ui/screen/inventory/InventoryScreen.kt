@file:OptIn(ExperimentalFoundationApi::class)

package com.github.k409.fitflow.ui.screen.inventory

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.item.CategorySelectHeader
import com.github.k409.fitflow.ui.common.item.InventoryItemCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }

    val inventoryUiState by inventoryViewModel.inventoryUiState.collectAsStateWithLifecycle()

    if (inventoryUiState is InventoryUiState.Loading) {
        FitFlowCircularProgressIndicator()
        return
    }
    val ownedItems = (inventoryUiState as InventoryUiState.Success).ownedItems

    Log.d("InventoryScreen", ownedItems[0].isPlaced.toString())
    val items = when (selectedCategoryIndex) {
        0 -> ownedItems.filter { it.type == "fish" }
        1 -> ownedItems.filter { it.type == "decoration" }
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
                removeButtonText = "Remove",
                onRemoveClick =
                {
                    Toast.makeText(
                        context,
                        "Removed from aquarium",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                removeButtonEnabled = item.isPlaced,
                addButtonText = "Add",
                onAddClick =
                {
                    Toast.makeText(
                        context,
                        "Added to aquarium",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                addButtonEnabled = !item.isPlaced,
                coinIcon = {},
                selectedCategoryIndex = selectedCategoryIndex,
            )
        }
    }
}
