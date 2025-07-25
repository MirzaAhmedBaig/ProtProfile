package com.mab.protprofile.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    @DocumentId
    val id: String = "",
    val addedBy: String = "",
    val date: String = "",
    val paymentMonth: Int? = null,
    val paymentYear: Int? = null,
    val paidTo: String? = null,
    val amount: Int? = null,
)