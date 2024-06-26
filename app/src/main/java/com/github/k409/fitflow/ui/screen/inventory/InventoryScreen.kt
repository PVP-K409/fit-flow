package com.github.k409.fitflow.ui.screen.inventory

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
import com.github.k409.fitflow.model.InventoryItem
import com.github.k409.fitflow.service.SnackbarManager
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.item.CategorySelectHeader
import com.github.k409.fitflow.ui.common.item.InventoryItemCard
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val language = context.resources.configuration.locales[0].language

    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }

    val inventoryUiState by inventoryViewModel.inventoryUiState.collectAsStateWithLifecycle()
    if (inventoryUiState is InventoryUiState.Loading) {
        FitFlowCircularProgressIndicator()
        return
    }
    val ownedItems = (inventoryUiState as InventoryUiState.Success).ownedItems

    val placedItemCounts = remember {
        mutableMapOf(
            "fish" to ownedItems.filter { it.placed && it.item.type == "fish" }.size,
            "decoration" to ownedItems.filter { it.placed && it.item.type == "decoration" }.size,
        )
    }

    val items = when (selectedCategoryIndex) {
        0 -> ownedItems.filter { it.item.type == "fish" }
        1 -> ownedItems.filter { it.item.type == "decoration" }
        else -> emptyList()
    }
    var mLastSnackbarTime: Long = 0
    val mNewSnackbarInterval = 2000 // milliseconds

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
        items(items) { inventoryItem ->
            val item = inventoryItem.item

            InventoryItemCard(
                modifier = Modifier,
                imageDownloadUrl = item.phases?.get("Regular") ?: item.image,
                name = item.title,
                description = item.localizedDescriptions[language] ?: item.description,
                removeButtonText = stringResource(R.string.remove),
                onRemoveClick =
                {
                    inventoryViewModel.updateInventoryItem(
                        InventoryItem(
                            item,
                            false,
                        ),
                    )
                    SnackbarManager.showMessage(context.getString(R.string.removed_from_aquarium))
                    placedItemCounts[item.type] = placedItemCounts[item.type]!! - 1
                },
                removeButtonEnabled = inventoryItem.placed,
                addButtonText = stringResource(R.string.add),
                onAddClick =
                {
                    if (placedItemCounts[item.type]!! >= 3) {
                        // Limit toast messages in case button is spammed
                        if (System.currentTimeMillis() - mLastSnackbarTime > mNewSnackbarInterval) {
                            SnackbarManager.showMessage(context.getString(R.string.aquarium_size_limit_reached_please_remove_some_items_before_adding_more))
                            mLastSnackbarTime = System.currentTimeMillis()
                        }
                    } else {
                        inventoryViewModel.updateInventoryItem(
                            InventoryItem(
                                item,
                                true,
                                offsetX = Random.nextInt(1, 600).toFloat(),
                            ),
                        )
                        SnackbarManager.showMessage(context.getString(R.string.added_to_aquarium))
                        placedItemCounts[item.type] = placedItemCounts[item.type]!! + 1
                    }
                },
                addButtonEnabled = !inventoryItem.placed,
                coinIcon = {},
            )
        }
    }
}
