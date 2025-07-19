package com.mab.protprofile.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    @DocumentId
    val id: String = "",
    val addedBy: String = "",
    val date: String = "",
    val transactionMonth: Int? = null,
    val transactionYear: Int? = null,
    val totalSale: Int? = null,
    val totalPurchase: Int? = null,
    val creditPurchase: Int? = null,
    val totalExpense: Int? = null,
    val totalProfit: Int? = null,
)
