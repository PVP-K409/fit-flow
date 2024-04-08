package com.github.k409.fitflow.ui.screens.userLevel

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LevelScreen(){
    LazyColumn {
        items(levels.size) { index ->
            val level = levels[index]
            LevelCard(
                modifier = Modifier.padding(8.dp),
                name = level.name,
                minXp = level.minXP,
                maxXp = level.maxXP,
            )
        }
    }
}
