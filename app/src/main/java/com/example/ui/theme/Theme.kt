package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BlockDropColorScheme = darkColorScheme(
    primary = Color(0xFF00F0FF),
    onPrimary = Color(0xFF0A0E17),
    secondary = Color(0xFFBD00FF),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFFFE600),
    background = Color(0xFF0A0E17),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF131B2E),
    onSurface = Color(0xFFF1F5F9)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BlockDropColorScheme,
        typography = Typography,
        content = content
    )
}
