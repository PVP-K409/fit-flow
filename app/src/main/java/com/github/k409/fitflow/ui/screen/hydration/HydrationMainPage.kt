package com.github.k409.fitflow.ui.screen.hydration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ModeEditOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.common.LocalSnackbarHostState
import com.github.k409.fitflow.ui.common.NumberPickerDialog
import com.github.k409.fitflow.ui.common.showSnackbarIfNotVisible
import com.github.k409.fitflow.ui.screen.hydration.component.HydrationStatisticsCard
import com.github.k409.fitflow.ui.screen.hydration.component.WaterIndicator
import kotlinx.coroutines.launch

@Composable
fun HydrationMainPage(
    viewModel: HydrationViewModel,
) {
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
                DrinkButton(cupSize = uiState.cupSize) {
                    viewModel.addWaterCup()
                    viewModel.scheduleWaterReminder()
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
                .fillMaxSize(),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrinkButton(
    modifier: Modifier = Modifier,
    cupSize: Int,
    onDrink: () -> Unit = {},
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    Row(
        modifier = modifier
            .defaultMinSize(
                minWidth = ButtonDefaults.MinWidth,
                minHeight = ButtonDefaults.MinHeight,
            )
            .clip(ButtonDefaults.filledTonalShape)
            .combinedClickable(
                onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbarIfNotVisible(
                            message = context.getString(R.string.hold_drink_button_message),
                            withDismissAction = true,
                        )
                    }
                },
                onLongClick = {
                    onDrink()
                },
            )
            .background(colorScheme.secondaryContainer)
            .border(BorderStroke(1.0.dp, colorScheme.outline), CircleShape)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.drink_ml, cupSize),
            color = colorScheme.onSecondaryContainer,
            style = LocalTextStyle.current.merge(MaterialTheme.typography.labelLarge),
        )
    }
}
