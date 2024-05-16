package com.github.k409.fitflow.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.PermissionsHandler
import com.github.k409.fitflow.ui.theme.FitFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        splashScreen.setKeepOnScreenCondition {
            mainActivityViewModel.isLoading()
        }

        setContent {
            val uiState by mainActivityViewModel.sharedUiState.collectAsStateWithLifecycle()

            when (uiState) {
                is SharedUiState.Loading -> {
                    FitFlowCircularProgressIndicator()
                }

                is SharedUiState.Success -> {
                    val sharedUiState = uiState as SharedUiState.Success

                    FitFlowTheme(
                        themePreferences = sharedUiState.themePreferences,
                    ) {
                        PermissionsHandler()
                        FitFlowApp(
                            sharedUiState = sharedUiState,
                        )
                    }
                }
            }
        }
    }
}
