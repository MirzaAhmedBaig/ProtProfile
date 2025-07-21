package com.mab.protprofile.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class AppConfigs(
    @DocumentId
    val id: String = "",
    val expenseTypes: Array<String> = arrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppConfigs

        if (!expenseTypes.contentEquals(other.expenseTypes)) return false

        return true
    }

    override fun hashCode(): Int {
        return expenseTypes.contentHashCode()
    }
}
