package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.data.VisualTheme

data class GameThemeColors(
    val background: Color,
    val backgroundGradientEnd: Color,
    val surface: Color,
    val surfaceBorder: Color,
    val onSurface: Color,
    val onSurfaceSecondary: Color,
    val gridBackground: Color,
    val gridLines: Color,
    val ghostFill: Color,
    val ghostBorder: Color,
    val accent: Color,
    val blockColors: List<Color>, // I, O, T, S, Z, J, L
    val garbageBlock: Color
)

object ThemePalettes {

    val NeonCyber = GameThemeColors(
        background = Color(0xFF0A0E17),
        backgroundGradientEnd = Color(0xFF111827),
        surface = Color(0xFF131B2E),
        surfaceBorder = Color(0xFF1E293B),
        onSurface = Color(0xFFF1F5F9),
        onSurfaceSecondary = Color(0xFF94A3B8),
        gridBackground = Color(0xFF070B12),
        gridLines = Color(0x1A00F0FF),
        ghostFill = Color(0x1F00F0FF),
        ghostBorder = Color(0x5500F0FF),
        accent = Color(0xFF00F0FF),
        blockColors = listOf(
            Color(0xFF00F0FF), // I - Electric Cyan
            Color(0xFFFFE600), // O - Radiant Yellow
            Color(0xFFBD00FF), // T - Cyber Purple
            Color(0xFF39FF14), // S - Neon Lime
            Color(0xFFFF073A), // Z - Laser Red
            Color(0xFF0066FF), // J - Deep Azure
            Color(0xFFFF7A00)  // L - Hyper Orange
        ),
        garbageBlock = Color(0xFF475569)
    )

    val MinimalistLight = GameThemeColors(
        background = Color(0xFFF8FAFC),
        backgroundGradientEnd = Color(0xFFEEF2F6),
        surface = Color(0xFFFFFFFF),
        surfaceBorder = Color(0xFFE2E8F0),
        onSurface = Color(0xFF0F172A),
        onSurfaceSecondary = Color(0xFF64748B),
        gridBackground = Color(0xFFF1F5F9),
        gridLines = Color(0x3394A3B8),
        ghostFill = Color(0x150F172A),
        ghostBorder = Color(0x400F172A),
        accent = Color(0xFF2563EB),
        blockColors = listOf(
            Color(0xFF0284C7), // I - Sky
            Color(0xFFEAB308), // O - Warm Amber
            Color(0xFF9333EA), // T - Violet
            Color(0xFF16A34A), // S - Forest Emerald
            Color(0xFFDC2626), // Z - Rose
            Color(0xFF2563EB), // J - Royal Blue
            Color(0xFFEA580C)  // L - Tangerine
        ),
        garbageBlock = Color(0xFF94A3B8)
    )

    val MidnightOled = GameThemeColors(
        background = Color(0xFF000000),
        backgroundGradientEnd = Color(0xFF050505),
        surface = Color(0xFF0D0D0D),
        surfaceBorder = Color(0xFF222222),
        onSurface = Color(0xFFFFFFFF),
        onSurfaceSecondary = Color(0xFFAAAAAA),
        gridBackground = Color(0xFF000000),
        gridLines = Color(0x22333333),
        ghostFill = Color(0x22FFFFFF),
        ghostBorder = Color(0x66FFFFFF),
        accent = Color(0xFF00FFCC),
        blockColors = listOf(
            Color(0xFF00FFCC), // I - Mint
            Color(0xFFFFF000), // O - Lemon
            Color(0xFFE040FB), // T - Magenta
            Color(0xFF00E676), // S - Spring
            Color(0xFFFF1744), // Z - Ruby
            Color(0xFF2979FF), // J - Blue
            Color(0xFFFF9100)  // L - Amber
        ),
        garbageBlock = Color(0xFF333333)
    )

    val RetroArcade = GameThemeColors(
        background = Color(0xFF1A102A),
        backgroundGradientEnd = Color(0xFF2E1A47),
        surface = Color(0xFF2A1647),
        surfaceBorder = Color(0xFF4C2A7D),
        onSurface = Color(0xFFFFEBFB),
        onSurfaceSecondary = Color(0xFFD4A5E8),
        gridBackground = Color(0xFF120A1E),
        gridLines = Color(0x33FF007F),
        ghostFill = Color(0x22FF007F),
        ghostBorder = Color(0x66FF007F),
        accent = Color(0xFFFF007F),
        blockColors = listOf(
            Color(0xFF00E5FF), // I
            Color(0xFFFFD600), // O
            Color(0xFFAA00FF), // T
            Color(0xFF00E676), // S
            Color(0xFFFF1744), // Z
            Color(0xFF2979FF), // J
            Color(0xFFFF6D00)  // L
        ),
        garbageBlock = Color(0xFF553C7B)
    )

    val SolarSunset = GameThemeColors(
        background = Color(0xFF1A0B2E),
        backgroundGradientEnd = Color(0xFF2E0836),
        surface = Color(0xFF281142),
        surfaceBorder = Color(0xFF4B1E78),
        onSurface = Color(0xFFFFF1F2),
        onSurfaceSecondary = Color(0xFFFDA4AF),
        gridBackground = Color(0xFF140723),
        gridLines = Color(0x26FB7185),
        ghostFill = Color(0x22FB7185),
        ghostBorder = Color(0x66FB7185),
        accent = Color(0xFFF43F5E),
        blockColors = listOf(
            Color(0xFF38BDF8), // I - Dusk Sky
            Color(0xFFFDE047), // O - Golden Hour
            Color(0xFFA855F7), // T - Twilight Purple
            Color(0xFF4ADE80), // S - Oasis Green
            Color(0xFFFB7185), // Z - Sunset Pink
            Color(0xFF6366F1), // J - Indigo
            Color(0xFFFB923C)  // L - Apricot Glow
        ),
        garbageBlock = Color(0xFF6D28D9)
    )

    fun getTheme(theme: VisualTheme): GameThemeColors = when (theme) {
        VisualTheme.NEON_CYBER -> NeonCyber
        VisualTheme.MINIMALIST_LIGHT -> MinimalistLight
        VisualTheme.MIDNIGHT_OLED -> MidnightOled
        VisualTheme.RETRO_ARCADE -> RetroArcade
        VisualTheme.SOLAR_SUNSET -> SolarSunset
    }
}
