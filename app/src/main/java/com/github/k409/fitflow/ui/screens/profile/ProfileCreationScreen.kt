package com.github.k409.fitflow.ui.screens.profile

import android.annotation.SuppressLint
import android.util.Log
import android.widget.NumberPicker
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.k409.fitflow.R

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileCreationScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    val profileDictionary: SnapshotStateMap<String, Int> = remember {
        mutableStateMapOf()
    }
    val genders = arrayOf("Male", "Female")

    // State variables for error messages
    var nameError by remember { mutableStateOf<String?>(null) }
    var wasValidated by remember { mutableStateOf(false) }

    // Function to validate and update error messages
    @Composable
    fun validate(): Boolean {
        wasValidated = true
        nameError = if (name.isEmpty()) stringResource(R.string.required_field) else null
        // Return true if there are no errors, indicating that the form is valid
        return profileDictionary.containsKey("age") && profileDictionary.containsKey("weight") && profileDictionary.containsKey("gender")
                && profileDictionary.containsKey("height") && name.isNotEmpty()
    }

    @Composable
    fun NumberPickerDialog(
        onDismissRequest: () -> Unit,
        onConfirmation: () -> Unit,
        dialogTitle: String,
        dialogText: String,
        minValue: Int,
        maxValue: Int,
        valueKey: String,
        displayedValues: Array<String>?
    ) {
        var currentValue =
            if (profileDictionary.containsKey(valueKey)) profileDictionary[valueKey]!! else minValue
        AlertDialog(
            title = {
                Text(text = dialogTitle)
            },
            text = {
                Text(text = dialogText)
                // initialize number picker widget
                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = { context ->
                        NumberPicker(context).apply {
                            setOnValueChangedListener { _, _, newValue ->
                                currentValue = newValue
                            }
                            this.minValue = minValue
                            this.maxValue = maxValue
                            this.value =
                                if (profileDictionary.containsKey(valueKey)) profileDictionary[valueKey]!! else minValue
                            if (displayedValues != null)
                                this.displayedValues = displayedValues
                        }
                    },
                    update = {}
                )
            },
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileDictionary[valueKey] = currentValue
                        onConfirmation()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DropdownMenu(
        dialogTitle: String,
        placeholderText: String,
        minValue: Int,
        maxValue: Int,
        valueKey: String,
        displayedValues: Array<String>?
    ) {
        var isExpanded by remember {
            mutableStateOf(false)
        }
        var currentValue = ""
        var error = !(profileDictionary.containsKey(valueKey))
        if (profileDictionary.containsKey(valueKey)) {
            currentValue = if (displayedValues != null) {
                // Int value should be mapped to its existing String counterpart
                displayedValues[profileDictionary[valueKey]!!]
            } else {
                // Does not have String counterpart
                profileDictionary[valueKey]!!.toString()
            }
        }
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            modifier = Modifier
                .fillMaxWidth(0.4f),
            onExpandedChange = { newValue ->
                isExpanded = newValue
            },
        ) {
            TextField(
                value = currentValue,
                onValueChange = {
                    error = !(profileDictionary.containsKey(valueKey))
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
            NumberPickerDialog(
                onDismissRequest = { isExpanded = false },
                onConfirmation = {
                    Log.d("ProfileCreationScreen", profileDictionary[valueKey].toString())
                    isExpanded = false
                },
                dialogTitle = dialogTitle,
                dialogText = "",
                minValue = minValue,
                maxValue = maxValue,
                valueKey = valueKey,
                displayedValues = displayedValues
            )
        }
        // Display error message
        if (error && wasValidated) {
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
            value = name,
            onValueChange = {
                name = it
                nameError = if (name.isEmpty()) context.getString(R.string.required_field) else null
            },
            placeholder = { Text(text = "Enter your name here") },
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(200.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.age),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.gender),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(75.dp),
            //modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column {
                // Age input
                DropdownMenu("Select your age", "Select", 5, 125, "age", null)
            }
            Column {
                // Gender input
                DropdownMenu("Select your gender", "Select", 0, 1, "gender", genders)
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(140.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.weight_kg),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.height_cm),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(75.dp),
            //modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column {
                // Weight input
                DropdownMenu("Select your weight", "Select", 10, 250, "weight", null)
            }
            Column {
                // Height input
                DropdownMenu("Select your height", "Select", 30, 250, "height", null)
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
        ) {
            var isClicked by remember { mutableStateOf(false) }

            Button(onClick = { isClicked = true }) {
                Text(stringResource(R.string.save))
            }
            if (isClicked) {
                isClicked = false

                if (validate()) {
                    val success: Boolean = profileViewModel.submitProfile(
                        name,
                        profileDictionary["age"]!!.toInt(),
                        genders[profileDictionary["gender"]!!],
                        profileDictionary["weight"]!!.toInt(),
                        profileDictionary["height"]!!.toInt(),
                    )
                    if (success) {
                        displayMessage(stringResource(R.string.profile_saved))
                        navigateToProfileSettingsScreen(navController = navController)
                    } else {
                        displayMessage("Something went wrong. Try again")
                    }
                }
            }
        }
    }
}
