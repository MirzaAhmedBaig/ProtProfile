package com.mab.protprofile.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    @DocumentId
    val number: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.USER,
    val investedAmount: Int = 0,
    val sharePercent: Float = 0f,
    val parent: Boolean? = null,
)
