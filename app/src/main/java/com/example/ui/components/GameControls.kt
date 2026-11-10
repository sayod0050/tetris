package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GameThemeColors
import kotlinx.coroutines.delay

@Composable
fun AutoRepeatGameButton(
    icon: ImageVector,
    label: String? = null,
    testTag: String,
    theme: GameThemeColors,
    size: Dp = 54.dp,
    opacity: Float = 0.85f,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            onClick()
            delay(160) // Initial DAS delay
            while (isPressed) {
                onClick()
                delay(45) // Repeat interval
            }
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isPressed) theme.accent.copy(alpha = 0.35f)
                else theme.surface.copy(alpha = opacity)
            )
            .border(
                1.5.dp,
                if (isPressed) theme.accent else theme.surfaceBorder.copy(alpha = opacity),
                RoundedCornerShape(14.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label ?: testTag,
                tint = if (isPressed) theme.accent else theme.onSurface,
                modifier = Modifier.size((size.value * 0.52f).dp)
            )
        }
    }
}

@Composable
fun SingleTapGameButton(
    icon: ImageVector,
    label: String? = null,
    testTag: String,
    theme: GameThemeColors,
    size: Dp = 54.dp,
    opacity: Float = 0.85f,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(size)
            .clip(if (isPrimary) CircleShape else RoundedCornerShape(14.dp))
            .background(
                if (isPrimary) {
                    if (isPressed) theme.accent.copy(alpha = 0.8f) else theme.accent.copy(alpha = opacity)
                } else {
                    if (isPressed) theme.accent.copy(alpha = 0.35f) else theme.surface.copy(alpha = opacity)
                }
            )
            .border(
                1.5.dp,
                if (isPrimary) theme.accent else theme.surfaceBorder.copy(alpha = opacity),
                if (isPrimary) CircleShape else RoundedCornerShape(14.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onClick()
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label ?: testTag,
                tint = if (isPrimary) theme.background else if (isPressed) theme.accent else theme.onSurface,
                modifier = Modifier.size((size.value * 0.52f).dp)
            )
            if (label != null) {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPrimary) theme.background else theme.onSurfaceSecondary
                )
            }
        }
    }
}

@Composable
fun GamepadOverlay(
    theme: GameThemeColors,
    buttonOpacity: Float,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onSoftDrop: () -> Unit,
    onHardDrop: () -> Unit,
    onRotateCw: () -> Unit,
    onRotateCcw: () -> Unit,
    onHold: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // D-Pad on Left: Left, Soft Drop, Right + Hard Drop
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top row: Hold button
            SingleTapGameButton(
                icon = Icons.Default.SwapHoriz,
                label = "HOLD",
                testTag = "control_hold",
                theme = theme,
                size = 50.dp,
                opacity = buttonOpacity,
                onClick = onHold
            )

            // D-Pad Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AutoRepeatGameButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    testTag = "control_left",
                    theme = theme,
                    size = 54.dp,
                    opacity = buttonOpacity,
                    onClick = onMoveLeft
                )

                AutoRepeatGameButton(
                    icon = Icons.Default.ArrowDownward,
                    testTag = "control_soft_drop",
                    theme = theme,
                    size = 54.dp,
                    opacity = buttonOpacity,
                    onClick = onSoftDrop
                )

                AutoRepeatGameButton(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    testTag = "control_right",
                    theme = theme,
                    size = 54.dp,
                    opacity = buttonOpacity,
                    onClick = onMoveRight
                )
            }
        }

        // Action Buttons on Right: Rotate CCW, Rotate CW, and Hard Drop
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Hard Drop Button
            SingleTapGameButton(
                icon = Icons.Default.KeyboardDoubleArrowDown,
                label = "DROP",
                testTag = "control_hard_drop",
                theme = theme,
                size = 50.dp,
                opacity = buttonOpacity,
                isPrimary = true,
                onClick = onHardDrop
            )

            // Rotation Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SingleTapGameButton(
                    icon = Icons.Default.RotateLeft,
                    label = "CCW",
                    testTag = "control_rotate_ccw",
                    theme = theme,
                    size = 54.dp,
                    opacity = buttonOpacity,
                    onClick = onRotateCcw
                )

                SingleTapGameButton(
                    icon = Icons.Default.RotateRight,
                    label = "CW",
                    testTag = "control_rotate_cw",
                    theme = theme,
                    size = 54.dp,
                    opacity = buttonOpacity,
                    onClick = onRotateCw
                )
            }
        }
    }
}
