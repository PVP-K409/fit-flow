package com.github.k409.fitflow.ui.screens.hydration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ModeEditOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.github.k409.fitflow.ui.common.Dialog
import com.github.k409.fitflow.ui.common.NumberPicker
import com.github.k409.fitflow.ui.common.PickerState.Companion.rememberPickerState
import com.github.k409.fitflow.ui.components.WaterIndicator
import com.github.k409.fitflow.ui.components.hydration.WaterIntakeLog
import com.github.k409.fitflow.ui.common.NumberPickerDialog

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
            .padding(horizontal = 16.dp),
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

        WaterIntakeLog(
            milliliters = uiState.yesterday.toLong(),
            thisWeek = uiState.thisWeek.toDouble(),
            thisMonth = uiState.thisMonth.toDouble(),
        )

        if (editCupSizeDialogState.value) {
            val cupValues = Array(300) { (it * 10 + 10).toString() }
            NumberPickerDialog(
                onDismissRequest = { editCupSizeDialogState.value = false },
                onConfirmation = { editCupSizeDialogState.value = false
                    viewModel.setCupSize(it.toInt()*10) },
                dialogTitle = stringResource(R.string.cup_size_ml),
                dialogText = "",
                minValue = 1,
                maxValue = 300,
                initialValue = uiState.cupSize/10,
                displayedValues = cupValues
            )
        }
    }
}

//@Composable
//fun EditCupSizeDialog(
//    onDismiss: () -> Unit,
//    onSaveClick: (Int) -> Unit,
//    currentCupSize: Int,
//) {
//    val numberPickerState = rememberPickerState(initialValue = currentCupSize)
//
//    val minCupSize = 10
//    val stepSize = 10
//    val maxCupSize = 3000
//
//    val items = remember {
//        ((minCupSize - stepSize)..(maxCupSize + stepSize) step stepSize).toList()
//    }
//
//    Dialog(
//        title = stringResource(R.string.cup_size_ml),
//        onSaveClick = {
//            onSaveClick(numberPickerState.value)
//            onDismiss()
//        },
//        onDismiss = onDismiss,
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            NumberPicker(
//                items = items,
//                state = numberPickerState,
//                indexStart = items.indexOf(currentCupSize).coerceAtLeast(0),
//                showDivider = false,
//                textStyle = MaterialTheme.typography.headlineLarge,
//            )
//        }
//    }
//}
