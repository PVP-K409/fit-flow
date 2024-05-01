package com.github.k409.fitflow.ui.screen.level

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import kotlinx.coroutines.launch

@Composable
fun LevelUpScreen(
    viewModel: LevelViewModel = hiltViewModel(),
    ) {
    val uiState by viewModel.levelUiState.collectAsState()

    when (uiState) {
        is LevelUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }

        is LevelUiState.Success -> {
            LevelUpScreenContent(viewModel = viewModel, uiState = uiState as LevelUiState.Success)
        }
    }
}
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun LevelUpScreenContent(viewModel: LevelViewModel, uiState: LevelUiState.Success) {
    val clicked = remember { mutableStateOf(false) }
    val level = levels.first { it.minXP <= uiState.user.xp && it.maxXP >= uiState.user.xp }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Congratulations! You have reached level ${level.id}!",
        )
        Row(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    clicked.value = true
                }
            ) {
                Text(
                    text = "Awesome!",
                )
            }
        }
    }

    if (clicked.value) {
        clicked.value = false

        coroutineScope.launch {
            //Log.d("LevelUpScreenContent", "Adding reward item to user inventory")
            viewModel.updateUserField("hasLeveledUp", false)
            viewModel.addRewardItemToUserInventory(level.id)
        }
    }
}