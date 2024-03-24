package com.github.k409.fitflow.ui.screens.aquarium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.components.aquarium.AquariumContent

@Composable
fun AquariumScreen(
    viewModel: AquariumViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState()

    when (val state = uiState.value) {
        is AquariumUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }

        is AquariumUiState.Success -> {
            AquariumContent(
                uiState = state,
                onHealthLevelChanged = viewModel::updateHealthLevel,
                onWaterLevelChanged = viewModel::updateWaterLevel,
            )
        }
    }
}
