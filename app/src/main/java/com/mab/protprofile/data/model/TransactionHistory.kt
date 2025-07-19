package com.mab.protprofile.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class TransactionHistory(
    @DocumentId
    val id: String = "",
    val transId: String = "",
    val date: Long = 0L,
    val editedBy: String = "",
    val changes: TransactionChange = TransactionChange()
)

@Serializable
data class TransactionChange(
    val totalSale: Int? = null,
    val totalPurchase: Int? = null,
    val creditPurchase: Int? = null,
    val totalExpense: Int? = null,
    val totalProfit: Int? = null,
)