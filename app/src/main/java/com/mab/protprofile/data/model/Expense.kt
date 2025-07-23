package com.mab.protprofile.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    @DocumentId
    val id: String = "",
    val expenseMonth: Int? = null,
    val expenseYear: Int? = null,
    val expenses: Map<String, Int> = mapOf(),
    val updatedAt: String = "",
    val updatedBy: String = "",
    val notes: String? = null,
)