package com.mab.protprofile.data.model

import android.content.Context
import androidx.annotation.StringRes

sealed class ErrorMessage {
    class StringError(
        val message: String,
    ) : ErrorMessage()

    class IdError(
        @param:StringRes val message: Int,
    ) : ErrorMessage()
}

fun ErrorMessage.getResolvedMessage(context: Context): String =
    when (this) {
        is ErrorMessage.StringError -> this.message
        is ErrorMessage.IdError -> context.getString(this.message)
    }
