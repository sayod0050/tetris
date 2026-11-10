package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.ui.theme.GameThemeColors

@Composable
fun HomeScreen(
    theme: GameThemeColors,
    onQuickPlay: () -> Unit,
    onModeSelect: () -> Unit,
    onStats: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blocks_bg")
    val animOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "falling_blocks"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(theme.background, theme.backgroundGradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background falling geometric cubes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cols = listOf(theme.blockColors[0], theme.blockColors[2], theme.blockColors[6], theme.accent)
            for (i in 0 until 12) {
                val seedX = ((i * 137) % size.width.toInt()).toFloat()
                val speedFactor = 0.5f + (i % 3) * 0.35f
                val seedY = ((animOffsetY * speedFactor + (i * 193)) % (size.height + 100)) - 50
                val blockSize = 24f + (i % 4) * 8f
                val color = cols[i % cols.size].copy(alpha = 0.12f)

                drawRoundRect(
                    color = color,
                    topLeft = Offset(seedX, seedY),
                    size = Size(blockSize, blockSize),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 480.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Title Banner
            Text(
                text = "BLOCK DROP",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = theme.accent,
                letterSpacing = 3.sp
            )
            Text(
                text = "PREMIUM FALLING-BLOCK PUZZLE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = theme.onSurfaceSecondary,
                letterSpacing = 1.8.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // Primary Quick Play Button
            Button(
                onClick = onQuickPlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("home_quick_play_button"),
                colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = theme.background,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "QUICK PLAY",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.background,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Game Mode Selection Button
            OutlinedButton(
                onClick = onModeSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("home_mode_select_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Category, contentDescription = null, tint = theme.onSurface)
                Text(
                    text = "SELECT MODE (5 MODES)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.onSurface,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Secondary Quick Navigation Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuCard(
                    icon = Icons.Default.BarChart,
                    title = "STATISTICS",
                    theme = theme,
                    testTag = "home_stats_button",
                    onClick = onStats,
                    modifier = Modifier.weight(1f)
                )

                MenuCard(
                    icon = Icons.Default.HelpOutline,
                    title = "HOW TO PLAY",
                    theme = theme,
                    testTag = "home_tutorial_button",
                    onClick = onTutorial,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuCard(
                    icon = Icons.Default.Settings,
                    title = "SETTINGS",
                    theme = theme,
                    testTag = "home_settings_button",
                    onClick = onSettings,
                    modifier = Modifier.weight(1f)
                )

                MenuCard(
                    icon = Icons.Default.Info,
                    title = "ABOUT & INFO",
                    theme = theme,
                    testTag = "home_about_button",
                    onClick = onAbout,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MenuCard(
    icon: ImageVector,
    title: String,
    theme: GameThemeColors,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(theme.surface)
            .border(1.dp, theme.surfaceBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = theme.accent,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = theme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
