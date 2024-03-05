package com.github.k409.fitflow.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.PermissionsHandler
import com.github.k409.fitflow.ui.theme.FitFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val sharedUiState by mainActivityViewModel.sharedUiState.collectAsStateWithLifecycle()

            FitFlowTheme(dynamicColor = false) {
                when (sharedUiState) {
                    is SharedUiState.Loading -> {
                        FitFlowCircularProgressIndicator()
                    }

                    is SharedUiState.Success -> {
                        Log.d("MainActivity", (sharedUiState as SharedUiState.Success).user.toString())
                        PermissionsHandler()
                        FitFlowApp(
                            sharedUiState = sharedUiState as SharedUiState.Success,
                        )
                    }
                }
            }
        }
    }
}
