package com.github.k409.fitflow.ui.screens.aquarium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.ui.components.aquarium.AquariumContent

@Composable
fun AquariumScreen(
    viewModel: AquariumViewModel = hiltViewModel(),
) {

    val uiState = viewModel.uiState.collectAsState()

    AquariumContent(
        modifier = Modifier,
        uiState = uiState.value,
        onHealthLevelChanged = viewModel::onHealthLevelChanged,
        onWaterLevelChanged = viewModel::onWaterLevelChanged,
        onFishChanged = viewModel::onFishChanged,
    )
}
