package com.mab.protprofile.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class AppConfigs(
    @DocumentId
    val id: String = "",
    val expenseTypes: List<String> = listOf(),
)
