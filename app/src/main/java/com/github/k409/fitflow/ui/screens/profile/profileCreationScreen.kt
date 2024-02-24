package com.github.k409.fitflow.ui.screens.profile

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.k409.fitflow.R
import com.github.k409.fitflow.ui.profile.ProfileRepository
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCreationScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val repository : ProfileRepository = ProfileRepository()

    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }

    // State variables for error messages
    var nameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }

    // Function to validate and update error messages
    fun validate(): Boolean {
        nameError = if (name.isEmpty()) "*Required field" else null
        ageError = if (age.isEmpty()) "*Required field" else null
        genderError = if (gender.isEmpty()) "*Required field" else null
        weightError = if (weight.isEmpty()) "*Required field" else null
        heightError = if (height.isEmpty()) "*Required field" else null

        // Return true if there are no errors, indicating that the form is valid
        return nameError == null && ageError == null && genderError == null && weightError == null
                && heightError == null
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GenderDropdownMenu() {
        var isExpanded by remember {
            mutableStateOf(false)
        }

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { newValue ->
                isExpanded = newValue
            }
        ) {
            TextField(
                value = gender,
                onValueChange = {
                    gender = it },
                isError = genderError != null,
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                placeholder = {
                    Text(text = "Please select your gender")
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = {
                    isExpanded = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = "Male")
                    },
                    onClick = {
                        gender = "Male"
                        isExpanded = false
                        genderError = if (gender.isEmpty()) "*Required field" else null
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = "Female")
                    },
                    onClick = {
                        gender = "Female"
                        isExpanded = false
                        genderError = if (gender.isEmpty()) "*Required field" else null
                    }
                ) }
        }
        // Display error message for Gender
        genderError?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
    @Composable
    fun displayMessage() {
        val context = LocalContext.current
        Column {
            Toast.makeText(context, "Profile saved", Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Name input
        Text(
            text = stringResource(id = R.string.user_name),
            style = MaterialTheme.typography.bodyLarge
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = {
                name = it
                nameError = if (name.isEmpty()) "*Required field" else null},
            //placeholder = { Text(text = "e.g. Bob") },
            isError = nameError != null,
        )
        // Display error message for Name
        nameError?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        // pattern for pure number input
        val pattern = remember { Regex("^\\d+\$") }
        // Age input
        Text(
            text = "Age",
            style = MaterialTheme.typography.bodyLarge
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = age,
            onValueChange = { if (it.isEmpty() || it.matches(pattern)) {
                age = it
                ageError = if (age.isEmpty()) "*Required field" else null
            } },
            //placeholder = { Text(text = "e.g. 18") },
            isError = ageError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        // Display error message for Age
        ageError?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        // Gender input
        Text(
            text = "Gender",
            style = MaterialTheme.typography.bodyLarge
        )

        GenderDropdownMenu()

        Spacer(modifier = Modifier.padding(8.dp))
        // Weight input - replace with dropdown
        Text(
            text = "Weight (kg)",
            style = MaterialTheme.typography.bodyLarge
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = weight,
            onValueChange = { if (it.isEmpty() || it.matches(pattern)) {
                weight = it
                weightError = if (weight.isEmpty()) "*Required field" else null
            } },
            //placeholder = { Text(text = "e.g. 80") },
            isError = weightError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        // Display error message for Weight
        weightError?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        // Height input - replace with dropdown
        Text(
            text = "Height (cm)",
            style = MaterialTheme.typography.bodyLarge
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = height,
            onValueChange = { if (it.isEmpty() || it.matches(pattern)) {
                height = it
                heightError = if (height.isEmpty()) "*Required field" else null} },
            //placeholder = { Text(text = "e.g. Bob") },
            isError = heightError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        // Display error message for Height
        heightError?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Column( modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            var isClicked by remember { mutableStateOf(false) }
            Button(onClick = { isClicked = true }) {
                Text("Save")
            }
            if (isClicked) {
                isClicked = false
                if (validate()) {
                    coroutineScope.launch {
                        repository.SubmitProfile(name, age.toInt(), gender, weight.toInt(), height.toInt())
                    }
                    displayMessage()
                    NavigateToProfileSettingsScreen(navController = navController)
                }
            }
        }
    }
}
