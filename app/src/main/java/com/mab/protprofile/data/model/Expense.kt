package com.mab.protprofile.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    @DocumentId
    val date: String = "",
    val expenses: Map<String, Int> = mapOf(),
    val notes: String = "",
)