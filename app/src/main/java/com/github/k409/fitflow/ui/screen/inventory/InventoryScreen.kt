package com.github.k409.fitflow.ui.screen.inventory

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.Item
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

    var placedItemCount by remember { mutableIntStateOf(ownedItems.filter { it.placed }.size) }

    val items = when (selectedCategoryIndex) {
        0 -> ownedItems.filter { it.type == "fish" }
        1 -> ownedItems.filter { it.type == "decoration" }
        else -> emptyList()
    }
    var mLastToastTime: Long = 0
    val mNewToastInterval = 2000 // milliseconds

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
                removeButtonText = stringResource(R.string.remove),
                onRemoveClick =
                {
                    inventoryViewModel.updateInventoryItem(
                        Item(
                            item.id,
                            item.title,
                            item.description,
                            item.price,
                            item.phases,
                            item.type,
                            item.image,
                            false,
                        ),
                    )
                    Toast.makeText(
                        context,
                        context.getString(R.string.removed_from_aquarium),
                        Toast.LENGTH_SHORT,
                    ).show()
                    placedItemCount--
                },
                removeButtonEnabled = item.placed,
                addButtonText = stringResource(R.string.add),
                onAddClick =
                {
                    if (placedItemCount >= 3) {
                        // Limit toast messages in case button is spammed
                        if (System.currentTimeMillis() - mLastToastTime > mNewToastInterval) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.aquarium_size_limit_reached_please_remove_some_items_before_adding_more),
                                Toast.LENGTH_SHORT,
                            ).show()
                            mLastToastTime = System.currentTimeMillis()
                        }
                    } else {
                        inventoryViewModel.updateInventoryItem(
                            Item(
                                item.id,
                                item.title,
                                item.description,
                                item.price,
                                item.phases,
                                item.type,
                                item.image,
                                true,
                            ),
                        )
                        Toast.makeText(
                            context,
                            context.getString(R.string.added_to_aquarium),
                            Toast.LENGTH_SHORT,
                        ).show()
                        placedItemCount++
                    }
                },
                addButtonEnabled = !item.placed,
                coinIcon = {},
                selectedCategoryIndex = selectedCategoryIndex,
            )
        }
    }
}
