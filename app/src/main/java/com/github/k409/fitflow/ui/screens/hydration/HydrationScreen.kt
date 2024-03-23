package com.github.k409.fitflow.ui.screens.hydration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ModeEditOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.NumberPickerDialog
import com.github.k409.fitflow.ui.components.hydration.WaterIndicator
import com.github.k409.fitflow.ui.components.hydration.HydrationStatisticsCard

@Composable
fun WaterLoggingScreen(
    viewModel: HydrationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val editCupSizeDialogState = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            WaterIndicator(
                totalWaterAmount = uiState.dailyGoal,
                usedWaterAmount = uiState.today,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                FilledTonalButton(
                    modifier = Modifier.align(Alignment.Center),
                    border = ButtonDefaults.outlinedButtonBorder,
                    onClick = {
                        viewModel.addWaterCup()
                        HydrationReminder().scheduleWaterReminder(context)
                    },
                ) {
                    Text(stringResource(R.string.drink_ml, uiState.cupSize))
                }

                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                        editCupSizeDialogState.value = true
                    },
                ) {
                    Icon(
                        modifier = Modifier
                            .size(18.dp)
                            .alpha(0.5f),
                        imageVector = Icons.Outlined.ModeEditOutline,
                        contentDescription = null,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(IntrinsicSize.Max)
                    .fillMaxWidth(),
            ) {
                HydrationStatisticsCard(
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    stats = uiState.stats,
                )
            }
        }

        if (editCupSizeDialogState.value) {
            val cupValues = Array(300) { (it * 10 + 10).toString() }

            NumberPickerDialog(
                onDismissRequest = { editCupSizeDialogState.value = false },
                onConfirmation = {
                    editCupSizeDialogState.value = false
                    viewModel.setCupSize(it.toInt() * 10)
                },
                dialogTitle = stringResource(R.string.cup_size_ml),
                dialogText = "",
                minValue = 1,
                maxValue = 300,
                initialValue = uiState.cupSize / 10,
                displayedValues = cupValues,
            )
        }
    }
}
