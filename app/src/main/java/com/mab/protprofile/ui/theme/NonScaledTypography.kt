package com.mab.protprofile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun NonScaledTypography(): Typography {
    val fontScale = LocalDensity.current.fontScale
    fun fixed(size: Float) = (size / fontScale).sp

    return Typography(
        displayLarge = TextStyle(fontSize = fixed(57f)),
        displayMedium = TextStyle(fontSize = fixed(45f)),
        displaySmall = TextStyle(fontSize = fixed(36f)),
        headlineLarge = TextStyle(fontSize = fixed(32f)),
        headlineMedium = TextStyle(fontSize = fixed(28f)),
        headlineSmall = TextStyle(fontSize = fixed(24f)),
        titleLarge = TextStyle(fontSize = fixed(22f)),
        titleMedium = TextStyle(fontSize = fixed(16f)),
        titleSmall = TextStyle(fontSize = fixed(14f)),
        bodyLarge = TextStyle(fontSize = fixed(16f)),
        bodyMedium = TextStyle(fontSize = fixed(14f)),
        bodySmall = TextStyle(fontSize = fixed(12f)),
        labelLarge = TextStyle(fontSize = fixed(14f)),
        labelMedium = TextStyle(fontSize = fixed(12f)),
        labelSmall = TextStyle(fontSize = fixed(11f)),
    )
}
