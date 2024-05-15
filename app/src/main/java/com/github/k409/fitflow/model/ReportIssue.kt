package com.github.k409.fitflow.model

data class ReportIssue(
    val title: String,
    val description: String,
    val createdDateTime: String,
    val reportedByUid: String,
    val reporterByEmail: String,
    val resolved: Boolean = false,
)
