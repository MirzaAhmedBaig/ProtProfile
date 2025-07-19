package com.mab.protprofile.ui.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


fun getCurrentDateTimeString(): String {
    val formatString = "dd/MM/yyyy hh:mm a"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(formatString)
        return currentDateTime.format(formatter)
    } else {
        val sdf = SimpleDateFormat(formatString, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date())
    }
}

@OptIn(ExperimentalTime::class)
fun getCurrentEpoch(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

fun formatDate(epochMillis: Long): String {
    val formatString = "dd/MM/yyyy hh:mm a"
    val sdf = SimpleDateFormat(formatString, Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(epochMillis))
}

fun monthName(month: Int?): String = when (month) {
    1 -> "Jan"
    2 -> "Feb"
    3 -> "Mar"
    4 -> "Apr"
    5 -> "May"
    6 -> "Jun"
    7 -> "Jul"
    8 -> "Aug"
    9 -> "Sep"
    10 -> "Oct"
    11 -> "Nov"
    12 -> "Dec"
    else -> "?"
}