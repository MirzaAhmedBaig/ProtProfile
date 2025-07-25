package com.mab.protprofile.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class InvestmentSummary(
    @DocumentId
    val id: String = "",
    val totalInvestment: Int = 0,
    val assets: Map<String, Int> = mapOf(),
)
