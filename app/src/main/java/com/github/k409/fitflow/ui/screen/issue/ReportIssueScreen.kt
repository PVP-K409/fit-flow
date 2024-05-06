package com.github.k409.fitflow.ui.screen.issue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import kotlinx.coroutines.launch

@Composable
fun ReportIssueScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    reportIssueViewModel: ReportIssueViewModel = hiltViewModel(),
) {
    var title by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()



    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.padding(bottom = 6.dp),
                text = stringResource(id = R.string.report_issue),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(R.string.fill_the_form_to_report_an_issue),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Light,
            )
        }

        TextField(
            modifier = Modifier,
            label = stringResource(R.string.title),
            onValueChange = { title = it },
        )

        TextArea(
            modifier = Modifier.height(150.dp),
            label = stringResource(R.string.detailed_description),
            onValueChange = { description = it },
        )

        Spacer(modifier = Modifier.height(32.dp))

        FilledTonalButton(onClick = {
            coroutineScope.launch {
                val success = reportIssueViewModel.reportIssue(
                    ReportIssueUiState(title, description)
                )

                if (success) {
                    navigateBack()
                }
            }
        }) {
            Text(text = stringResource(R.string.submit))
        }
    }


}

@Composable
private fun TextArea(
    modifier: Modifier = Modifier,
    label: String,
    initialTextFieldValue: String = "",
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    var text by remember { mutableStateOf(TextFieldValue(initialTextFieldValue)) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier.height(100.dp),
        value = text,
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(onNext = {
            val isMoved = focusManager.moveFocus(FocusDirection.Down)
            if (!isMoved) {
                focusManager.clearFocus()
            }
        }),
        onValueChange = {
            text = it
            onValueChange(it.text)
        },
    )
}

@Composable
private fun TextField(
    modifier: Modifier = Modifier,
    label: String,
    initialTextFieldValue: String = "",
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    var text by remember { mutableStateOf(TextFieldValue(initialTextFieldValue)) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = text,
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(onNext = {
            val isMoved = focusManager.moveFocus(FocusDirection.Down)
            if (!isMoved) {
                focusManager.clearFocus()
            }
        }),
        onValueChange = {
            text = it
            onValueChange(it.text)
        },
        singleLine = singleLine,
        modifier = modifier
    )
}