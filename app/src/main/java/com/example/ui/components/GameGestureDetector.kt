package com.example.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

/**
 * High-performance gesture detector for block sliding, dropping, rotating, and holding.
 */
fun Modifier.gameGestureControls(
    sensitivity: Float = 1.0f,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onSoftDrop: () -> Unit,
    onHardDrop: () -> Unit,
    onRotate: () -> Unit,
    onHold: () -> Unit
): Modifier = this
    .pointerInput(sensitivity) {
        detectTapGestures(
            onTap = { onRotate() },
            onDoubleTap = { onHardDrop() }
        )
    }
    .pointerInput(sensitivity) {
        var totalDragX = 0f
        var totalDragY = 0f
        val stepX = (32f / sensitivity).coerceAtLeast(16f)
        val stepY = (36f / sensitivity).coerceAtLeast(18f)

        detectDragGestures(
            onDragStart = {
                totalDragX = 0f
                totalDragY = 0f
            },
            onDragEnd = {
                // If fast upward flick
                if (totalDragY < -120f && abs(totalDragX) < 100f) {
                    onHold()
                } else if (totalDragY > 200f && abs(totalDragX) < 100f) {
                    onHardDrop()
                }
            },
            onDrag = { change, dragAmount ->
                change.consume()
                totalDragX += dragAmount.x
                totalDragY += dragAmount.y

                // Horizontal movement
                if (abs(totalDragX) >= stepX) {
                    if (totalDragX > 0) {
                        onMoveRight()
                    } else {
                        onMoveLeft()
                    }
                    totalDragX = 0f
                }

                // Vertical downward soft drop
                if (totalDragY >= stepY) {
                    onSoftDrop()
                    totalDragY = 0f
                }
            }
        )
    }
