package com.example.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = WinDarkKeyAccent,
    background = WinDarkBg,
    surface = WinDarkSurface,
    onPrimary = WinDarkTextPrimary,
    onBackground = WinDarkTextPrimary,
    onSurface = WinDarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = WinLightKeyAccent,
    background = WinLightBg,
    surface = WinLightSurface,
    onPrimary = WinDarkTextPrimary,
    onBackground = WinLightTextPrimary,
    onSurface = WinLightTextPrimary
)

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
