package com.github.k409.fitflow.ui.screens.market

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.ui.components.item.CategorySelectHeader
import com.github.k409.fitflow.ui.components.item.InventoryItemCard
import com.github.k409.fitflow.ui.components.item.mockDecorations
import com.github.k409.fitflow.ui.components.item.mockFishes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketScreen() {
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
                onItemSelected = { selectedCategoryIndex = it },
            )
        }
        items(items) { item ->
            InventoryItemCard(
                modifier = Modifier,
                painter = item.imageResId,
                name = item.name,
                description = item.description,
                removeButtonText = "Sell for: 5",
                onRemoveClick =
                {
                    Toast.makeText(
                        context,
                        item.name + " has been sold",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                addButtonText = "Buy for: 10",
                onAddClick =
                {
                    Toast.makeText(
                        context,
                        item.name + " has been added to your inventory",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                // Replace with designed coin icon later
                coinIcon = { Icon(Icons.Filled.CurrencyBitcoin,"",tint = MaterialTheme.colorScheme.onSurface) }
            )
        }
    }
}