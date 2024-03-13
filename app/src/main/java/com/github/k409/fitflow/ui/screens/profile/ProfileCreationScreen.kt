package com.github.k409.fitflow.ui.screens.profile

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.ui.common.NumberPickerDialog
import com.github.k409.fitflow.ui.screens.settings.SettingsViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileCreationScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by settingsViewModel.currentUser.collectAsState(initial = User())

    var name by rememberSaveable(currentUser.name) { mutableStateOf(currentUser.name) }
    var dateOfBirth by rememberSaveable(currentUser.dateOfBirth) { mutableStateOf(currentUser.dateOfBirth) }
    var gender by rememberSaveable(currentUser.gender) { mutableStateOf(currentUser.gender) }
    var weight by rememberSaveable(currentUser.weight) { mutableIntStateOf(currentUser.weight.toInt()) }
    var height by rememberSaveable(currentUser.height) { mutableIntStateOf(currentUser.height.toInt()) }
    var fitnessLevel by rememberSaveable(currentUser.fitnessLevel) { mutableStateOf(currentUser.fitnessLevel) }

    val genders = arrayOf(stringResource(id = R.string.male), stringResource(id = R.string.female))
    val fitnessLevels = arrayOf(stringResource(R.string.beginner),
        stringResource(R.string.intermediate), stringResource(R.string.advanced),
        stringResource(R.string.professional)
    )

    // State variables for error messages
    var nameError by remember { mutableStateOf<String?>(null) }
    var wasValidated by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf<Boolean?>(null) }

    // Function to validate and update error messages
    @Composable
    fun validate(): Boolean {
        wasValidated = true
        nameError = if (name.isEmpty()) stringResource(R.string.required_field) else null
        // Return true if all required field are filled
        return name.isNotEmpty() && dateOfBirth.isNotEmpty() && gender.isNotEmpty() && weight != 0 &&
                height != 0
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DropdownMenu(
        dialogTitle: String,
        placeholderText: String,
        onConfirmation: (String) -> Unit,
        minValue: Int,
        maxValue: Int,
        initialValue: String,
        displayedValues: Array<String>?,
        isRequired: Boolean,
        isNumberPicker: Boolean = true,
    ) {
        var isExpanded by remember {
            mutableStateOf(false)
        }
        var error = initialValue.isEmpty() && isRequired
        val currentValue = if (displayedValues != null && initialValue.isNotEmpty()) displayedValues.indexOf(initialValue).toString() else initialValue

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            modifier = Modifier
                .fillMaxWidth(0.45f),
            onExpandedChange = { newValue ->
                isExpanded = newValue
            },
        ) {
            TextField(
                value = initialValue,

                onValueChange = {
                    error = currentValue.isEmpty()
                },
                isError = error && wasValidated,
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                placeholder = {
                    Text(text = placeholderText)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .menuAnchor(),
            )
        }
        if (isExpanded) {
            if (isNumberPicker) {
                NumberPickerDialog(
                    onDismissRequest = { isExpanded = false },
                    onConfirmation = {
                        onConfirmation(it)
                        isExpanded = false
                    },
                    dialogTitle = dialogTitle,
                    dialogText = "",
                    minValue = minValue,
                    maxValue = maxValue,
                    initialValue = if (currentValue.isEmpty()) minValue else currentValue.toInt(),
                    displayedValues = displayedValues,
                )
            } else {
                val datePickerState = rememberDatePickerState(yearRange = LocalDate.now().year-120..<LocalDate.now().year)
                DatePickerDialog(
                    onDismissRequest = {
                        isExpanded = false
                    },
                    confirmButton = {
                        TextButton(
                            enabled = datePickerState.selectedDateMillis != null,
                            onClick = {
                                isExpanded = false
                                datePickerState.selectedDateMillis?.let {
                                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                }?.let {
                                    Log.d("ProfileScreen", it)
                                    onConfirmation(it)
                                }
                            },
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                isExpanded = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
        // Display error message
        if (error && wasValidated && isRequired) {
            Text(
                text = stringResource(id = R.string.required_field),
                color = Color.Red,
            )
        }
    }

    @Composable
    fun displayMessage(message: String) {
        Column {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Name input
        Text(
            text = stringResource(id = R.string.user_name),
            style = MaterialTheme.typography.bodyLarge,
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 20,
            value = name,
            onValueChange = {
                name = it
                nameError = if (name.isEmpty()) context.getString(R.string.required_field) else null
            },
            placeholder = { Text(text = stringResource(R.string.enter_your_name_here)) },
            isError = nameError != null,
        )
        // Display error message for Name
        nameError?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(),
            // .padding(2.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.date_of_birth),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(4.dp),
                )
                DropdownMenu(
                    stringResource(R.string.select_your_age),
                    stringResource(R.string.select),
                    { dateOfBirth = it },
                    5,
                    125,
                    dateOfBirth,
                    null,
                    isRequired = true,
                    false,
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.gender),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(4.dp),
                )
                DropdownMenu(
                    stringResource(R.string.select_your_gender),
                    stringResource(R.string.select),
                    { gender = genders[it.toInt()] },
                    0,
                    1,
                    gender,
                    genders,
                    true,
                )
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(),
            // .padding(8.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.weight_kg),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(4.dp),
                )
                DropdownMenu(
                    stringResource(R.string.select_your_weight),
                    stringResource(R.string.select),
                    { weight = it.toInt() },
                    10,
                    250,
                    if (weight != 0) weight.toString() else "",
                    null,
                    true,
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.height_cm),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(4.dp),
                )
                DropdownMenu(
                    stringResource(R.string.select_your_height),
                    stringResource(R.string.select),
                    { height = it.toInt() },
                    30,
                    250,
                    if (height != 0) height.toString() else "",
                    null,
                    true,
                )
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        //HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.tertiary)

        FlowRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(),
            // .padding(8.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.fitness_level),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(4.dp),
                )
                DropdownMenu(
                    stringResource(R.string.select_your_fitness_level),
                    stringResource(R.string.select),
                    { fitnessLevel = fitnessLevels[it.toInt()] },
                    0,
                    3,
                    fitnessLevel,
                    fitnessLevels,
                    false,
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.Bottom),
                verticalArrangement = Arrangement.Center,
                //horizontalAlignment = Alignment.End,
            ) {
                var isClicked by remember { mutableStateOf(false) }

                Button(onClick = { isClicked = true }) {
                    Text(stringResource(R.string.save))
                }
                if (isClicked) {
                    isClicked = false
                    if (validate()) {
                        coroutineScope.launch {
                            success = profileViewModel.submitProfile(
                                currentUser.uid,
                                name,
                                dateOfBirth,
                                gender,
                                weight,
                                height,
                                fitnessLevel,
                            )
                        }
                    }
                }
                if (success == true) {
                    success = null
                    displayMessage(stringResource(R.string.profile_saved))
                    navController.navigateUp()
                } else if (success == false) {
                    displayMessage(stringResource(R.string.something_went_wrong_try_again))
                    success = null
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))


    }
}
