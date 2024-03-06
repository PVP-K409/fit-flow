package com.github.k409.fitflow.ui.screens.profile

import android.annotation.SuppressLint
import android.util.Log
import android.widget.NumberPicker
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.github.k409.fitflow.model.User
import com.github.k409.fitflow.ui.navigation.NavRoutes
import com.github.k409.fitflow.ui.screens.settings.SettingsViewModel

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileCreationScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val currentUser by settingsViewModel.currentUser.collectAsState(initial = User())

    var name by rememberSaveable(currentUser.name) { mutableStateOf(currentUser.name) }
    val genders = arrayOf(stringResource(id = R.string.male), stringResource(id = R.string.female))

    val profileDictionary = remember { mutableStateMapOf<String, Int>() }
    val profileFields = listOf("gender", "age", "weight", "height")
    val currentValues: List<Int> = listOf(
        genders.indexOf(currentUser.gender),
        currentUser.age,
        currentUser.weight.toInt(),
        currentUser.height.toInt(),
    )
    // If gender value is set, then other required profile values are already filled as well
    if (currentValues[0] != -1 && profileDictionary.isEmpty()) {
        for (i in profileFields.indices) {
            profileDictionary[profileFields[i]] = currentValues[i]
        }
    }

    // Log.d("ProfileCreationScreen", currentUser.name)
    // Log.d("ProfileCreationScreen2", genders.indexOf(currentUser.gender).toString())

    // State variables for error messages
    var nameError by remember { mutableStateOf<String?>(null) }
    var wasValidated by remember { mutableStateOf(false) }

    // Function to validate and update error messages
    @Composable
    fun validate(): Boolean {
        wasValidated = true
        nameError = if (name.isEmpty()) stringResource(R.string.required_field) else null
        // Return true if there are no errors, indicating that the form is valid
        return profileDictionary.containsKey("age") && profileDictionary.containsKey("weight") && profileDictionary.containsKey(
            "gender",
            ) &&
            profileDictionary.containsKey("height") && name.isNotEmpty()
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
        displayedValues: Array<String>?,
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
                            if (displayedValues != null) {
                                this.displayedValues = displayedValues
                            }
                        }
                    },
                    update = {},
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
                    },
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
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
        displayedValues: Array<String>?,
    ) {
        var isExpanded by remember {
            mutableStateOf(false)
        }
        var currentValue = ""
        var error = !(profileDictionary.containsKey(valueKey))
        if (profileDictionary.containsKey(valueKey)) {
            if (displayedValues != null) {
                // Int value should be mapped to its existing String counterpart
                currentValue = displayedValues[profileDictionary[valueKey]!!]
            } else if (profileDictionary[valueKey] != 0) {
                // Does not have String counterpart
                currentValue = profileDictionary[valueKey]!!.toString()
            }
        }
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            modifier = Modifier
                .fillMaxWidth(0.45f),
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
                displayedValues = displayedValues,
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
                    text = stringResource(R.string.age),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(4.dp),
                )
                DropdownMenu(
                    stringResource(R.string.select_your_age),
                    stringResource(R.string.select),
                    5,
                    125,
                    "age",
                    null,
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.gender),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(4.dp),
                )
                DropdownMenu(stringResource(R.string.select_your_gender), stringResource(R.string.select), 0, 1, "gender", genders)
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
                DropdownMenu(stringResource(R.string.select_your_weight), stringResource(R.string.select), 10, 250, "weight", null)
            }
            Column {
                Text(
                    text = stringResource(R.string.height_cm),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(4.dp),
                )
                DropdownMenu(stringResource(R.string.select_your_height), stringResource(R.string.select), 30, 250, "height", null)
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
                        currentUser.uid,
                        name,
                        profileDictionary["age"]!!.toInt(),
                        genders[profileDictionary["gender"]!!],
                        profileDictionary["weight"]!!.toInt(),
                        profileDictionary["height"]!!.toInt(),
                    )
                    if (success) {
                        displayMessage(stringResource(R.string.profile_saved))
                        navController.navigate(NavRoutes.Settings.route)
                    } else {
                        displayMessage(stringResource(R.string.something_went_wrong_try_again))
                    }
                }
            }
        }
    }
}
