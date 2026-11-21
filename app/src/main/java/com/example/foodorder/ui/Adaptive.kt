package com.example.foodorder.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/** Kelas lebar layar sederhana (mengikuti breakpoint Material: 600dp dan 840dp). */
enum class WidthClass { Compact, Medium, Expanded }

@Composable
fun rememberWidthClass(): WidthClass {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return when {
        widthDp < 600 -> WidthClass.Compact
        widthDp < 840 -> WidthClass.Medium
        else -> WidthClass.Expanded
    }
}
