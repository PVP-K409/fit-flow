package com.github.k409.fitflow.ui.screen.aquarium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.screen.aquarium.component.AquariumContent

@Composable
fun AquariumScreen(
    viewModel: AquariumViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val uiState = viewModel.uiState.collectAsState()

    when (val state = uiState.value) {
        is AquariumUiState.Loading -> {
            FitFlowCircularProgressIndicator()
        }

        is AquariumUiState.Success -> {
            AquariumContent(
                uiState = state,
                navController = navController,
            )
        }
    }
}
