package com.mab.protprofile.ui.ext

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mab.protprofile.ui.Constants
import java.net.URLDecoder

inline fun <reified E> NavBackStackEntry.getCustomArg(key: String): E? {
    val json = arguments?.getString(key)
    return json?.let {
        val decoded = URLDecoder.decode(it, "UTF-8")
        val type = object : TypeToken<E>() {}.type
        Gson().fromJson(decoded, type)
    }
}

fun NavHostController.refresh() {
    previousBackStackEntry
        ?.savedStateHandle
        ?.set(Constants.SHOULD_REFRESH_KEY, true)
}

fun String.capitalizeWords(): String =
    this.split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
