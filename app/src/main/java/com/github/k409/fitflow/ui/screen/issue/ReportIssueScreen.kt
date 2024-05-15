package com.github.k409.fitflow.ui.screen.issue

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.util.GITHUB_ISSUES_URL
import kotlinx.coroutines.launch

@Composable
fun ReportIssueScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    reportIssueViewModel: ReportIssueViewModel = hiltViewModel(),
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        ReportIssueCard(
            reportIssueViewModel = reportIssueViewModel,
            onNavigateBack = navigateBack,
            onReportIssue = { reportIssueViewModel.reportIssue(it) },
        )

        GithubIssueCard()
    }
}

@Composable
private fun ReportIssueCard(
    modifier: Modifier = Modifier,
    reportIssueViewModel: ReportIssueViewModel,
    onNavigateBack: () -> Unit,
    onReportIssue: suspend (ReportIssueUiState) -> Boolean,
) {
    var title by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

    ElevatedCard(
        modifier = modifier
            .padding(16.dp),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
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
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.title),
                onValueChange = { title = it },
            )

            TextArea(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                label = stringResource(R.string.detailed_description),
                onValueChange = { description = it },
            )

            Spacer(modifier = Modifier.height(32.dp))

            FilledTonalButton(onClick = {
                coroutineScope.launch {
                    val success = onReportIssue(
                        ReportIssueUiState(
                            title = title,
                            description = description,
                        ),
                    )

                    if (success) {
                        onNavigateBack()
                    }
                }
            }) {
                Text(text = stringResource(R.string.submit))
            }
        }
    }
}

@Composable
private fun GithubIssueCard(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val githubIntent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(GITHUB_ISSUES_URL),
        )
    }

    ElevatedCard(
        modifier = modifier
            .padding(16.dp),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                modifier = Modifier.padding(bottom = 6.dp),
                text = stringResource(R.string.write_a_github_issue),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )

            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(R.string.write_github_issue_description),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Light,
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                FilledTonalButton(onClick = {
                    context.startActivity(githubIntent)
                }) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.github_mark),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = stringResource(R.string.write_a_github_issue))
                }
            }
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
            keyboardType = keyboardType,
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
    singleLine: Boolean = true,
) {
    var text by remember { mutableStateOf(TextFieldValue(initialTextFieldValue)) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = text,
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = keyboardType,
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
        modifier = modifier,
    )
}
