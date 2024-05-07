package com.github.k409.fitflow.ui.screen.issue

import androidx.lifecycle.ViewModel
import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.ReportIssue
import com.github.k409.fitflow.service.SnackbarManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ReportIssueViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ViewModel() {

    suspend fun reportIssue(state: ReportIssueUiState): Boolean {
        if (!validate(state)) {
            SnackbarManager.showNotDuplicateMessage(R.string.please_fill_all_fields)

            return false
        }

        val currentUser = auth.currentUser ?: return false

        val reportIssue = ReportIssue(
            title = state.title,
            description = state.description,
            createdDateTime = LocalDateTime.now().toString(),
            reportedByUid = currentUser.uid,
            reporterByEmail = currentUser.email ?: "",
        )

        firestore.collection("issues")
            .add(reportIssue)
            .await()

        SnackbarManager.showNotDuplicateMessage(R.string.issue_reported)

        return true
    }

    private fun validate(reportIssue: ReportIssueUiState): Boolean {
        return reportIssue.title.isNotBlank() && reportIssue.description.isNotBlank()
    }
}

data class ReportIssueUiState(
    val title: String,
    val description: String,
)
