package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.game.Board
import com.example.game.ParticleManager
import com.example.model.Piece
import com.example.ui.theme.GameThemeColors

@Composable
fun GameBoardCanvas(
    board: Board,
    currentPiece: Piece?,
    ghostPiece: Piece?,
    particleManager: ParticleManager,
    theme: GameThemeColors,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(10f / 20f)
            .testTag("game_board_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = size.width / board.width
            val cellHeight = size.height / board.height

            // 1. Draw Grid Background
            drawRect(
                color = theme.gridBackground,
                topLeft = Offset.Zero,
                size = size
            )

            // 2. Draw Subtle Grid Lines
            for (x in 1 until board.width) {
                drawLine(
                    color = theme.gridLines,
                    start = Offset(x * cellWidth, 0f),
                    end = Offset(x * cellWidth, size.height),
                    strokeWidth = 1f
                )
            }
            for (y in 1 until board.height) {
                drawLine(
                    color = theme.gridLines,
                    start = Offset(0f, y * cellHeight),
                    end = Offset(size.width, y * cellHeight),
                    strokeWidth = 1f
                )
            }

            // 3. Draw Locked Blocks on Grid
            for (y in 0 until board.height) {
                for (x in 0 until board.width) {
                    val colorIndex = board.grid[y][x]
                    if (colorIndex != null) {
                        val blockColor = if (colorIndex in theme.blockColors.indices) {
                            theme.blockColors[colorIndex]
                        } else {
                            theme.garbageBlock
                        }
                        drawBeveledBlock(
                            x = x * cellWidth,
                            y = y * cellHeight,
                            width = cellWidth,
                            height = cellHeight,
                            color = blockColor
                        )
                    }
                }
            }

            // 4. Draw Ghost Piece (if visible and not overlapping current)
            if (ghostPiece != null && currentPiece != null && ghostPiece.y != currentPiece.y) {
                val ghostColor = theme.blockColors.getOrElse(ghostPiece.type.colorIndex) { theme.accent }
                for (cell in ghostPiece.cells) {
                    val gx = cell.first
                    val gy = cell.second
                    if (gy in 0 until board.height && gx in 0 until board.width) {
                        drawGhostBlock(
                            x = gx * cellWidth,
                            y = gy * cellHeight,
                            width = cellWidth,
                            height = cellHeight,
                            color = ghostColor
                        )
                    }
                }
            }

            // 5. Draw Active Falling Piece
            if (currentPiece != null) {
                val pieceColor = theme.blockColors.getOrElse(currentPiece.type.colorIndex) { theme.accent }
                for (cell in currentPiece.cells) {
                    val px = cell.first
                    val py = cell.second
                    if (py in 0 until board.height && px in 0 until board.width) {
                        drawBeveledBlock(
                            x = px * cellWidth,
                            y = py * cellHeight,
                            width = cellWidth,
                            height = cellHeight,
                            color = pieceColor,
                            isGlow = true
                        )
                    }
                }
            }

            // 6. Draw Particle Bursts
            for (p in particleManager.particles) {
                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.size,
                    center = Offset(p.x * cellWidth, p.y * cellHeight)
                )
            }

            // 7. Draw Floating Banner Announcements (QUAD!, COMBO x4, etc.)
            for (banner in particleManager.banners) {
                val measuredText = textMeasurer.measure(
                    text = AnnotatedString(banner.text),
                    style = TextStyle(
                        color = banner.color.copy(alpha = banner.alpha),
                        fontSize = (20 * banner.scale).sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                val textX = (size.width - measuredText.size.width) / 2f
                val textY = banner.currentY * cellHeight

                // Draw glowing shadow
                drawText(
                    textMeasurer = textMeasurer,
                    text = banner.text,
                    style = TextStyle(
                        color = Color.Black.copy(alpha = banner.alpha * 0.8f),
                        fontSize = (20 * banner.scale).sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    topLeft = Offset(textX + 2f, textY + 2f)
                )
                // Draw text
                drawText(
                    textMeasurer = textMeasurer,
                    text = banner.text,
                    style = TextStyle(
                        color = banner.color.copy(alpha = banner.alpha),
                        fontSize = (20 * banner.scale).sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    topLeft = Offset(textX, textY)
                )
            }

            // 8. Draw Border around Board
            drawRoundRect(
                color = theme.surfaceBorder,
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 3f)
            )
        }
    }
}

private fun DrawScope.drawBeveledBlock(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: Color,
    isGlow: Boolean = false
) {
    val padding = 1.5f
    val rx = x + padding
    val ry = y + padding
    val rw = width - (padding * 2)
    val rh = height - (padding * 2)
    val corner = CornerRadius(rw * 0.18f, rh * 0.18f)

    // Base fill
    drawRoundRect(
        color = color,
        topLeft = Offset(rx, ry),
        size = Size(rw, rh),
        cornerRadius = corner,
        style = Fill
    )

    // Top-left highlight (lighter glass reflection)
    val highlightPath = Path().apply {
        moveTo(rx, ry + rh)
        lineTo(rx, ry)
        lineTo(rx + rw, ry)
        lineTo(rx + rw * 0.8f, ry + rh * 0.2f)
        lineTo(rx + rw * 0.2f, ry + rh * 0.2f)
        lineTo(rx + rw * 0.2f, ry + rh * 0.8f)
        close()
    }
    drawPath(
        path = highlightPath,
        color = Color.White.copy(alpha = 0.35f)
    )

    // Bottom-right shade (depth)
    val shadePath = Path().apply {
        moveTo(rx + rw, ry)
        lineTo(rx + rw, ry + rh)
        lineTo(rx, ry + rh)
        lineTo(rx + rw * 0.2f, ry + rh * 0.8f)
        lineTo(rx + rw * 0.8f, ry + rh * 0.8f)
        lineTo(rx + rw * 0.8f, ry + rh * 0.2f)
        close()
    }
    drawPath(
        path = shadePath,
        color = Color.Black.copy(alpha = 0.35f)
    )

    // Outer crisp stroke
    drawRoundRect(
        color = if (isGlow) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.2f),
        topLeft = Offset(rx, ry),
        size = Size(rw, rh),
        cornerRadius = corner,
        style = Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawGhostBlock(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: Color
) {
    val padding = 2f
    val rx = x + padding
    val ry = y + padding
    val rw = width - (padding * 2)
    val rh = height - (padding * 2)
    val corner = CornerRadius(rw * 0.18f, rh * 0.18f)

    // Faint fill
    drawRoundRect(
        color = color.copy(alpha = 0.15f),
        topLeft = Offset(rx, ry),
        size = Size(rw, rh),
        cornerRadius = corner,
        style = Fill
    )

    // Dotted or dashed-like crisp outline
    drawRoundRect(
        color = color.copy(alpha = 0.55f),
        topLeft = Offset(rx, ry),
        size = Size(rw, rh),
        cornerRadius = corner,
        style = Stroke(width = 1.5f)
    )
}
