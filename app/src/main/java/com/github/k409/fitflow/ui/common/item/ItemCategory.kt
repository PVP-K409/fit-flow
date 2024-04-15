package com.github.k409.fitflow.ui.common.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Phishing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class ItemCategory(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val categories = listOf(
    ItemCategory(
        title = "Fishes",
        selectedIcon = Icons.Filled.Phishing,
        unselectedIcon = Icons.Outlined.Phishing,
    ),
    ItemCategory(
        title = "Decorations",
        selectedIcon = Icons.Filled.Grass,
        unselectedIcon = Icons.Outlined.Grass,
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectHeader(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        categories.forEachIndexed { index, item ->
            SegmentedButton(
                icon = {},
                selected = selectedItemIndex == index,
                onClick = {
                    onItemSelected(index)
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, categories.size),
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
