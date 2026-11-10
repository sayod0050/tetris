package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TetrominoType
import com.example.ui.theme.GameThemeColors

@Composable
fun MiniPieceView(
    type: TetrominoType,
    theme: GameThemeColors,
    modifier: Modifier = Modifier,
    cellSizeDp: Float = 14f,
    alpha: Float = 1f
) {
    Box(
        modifier = modifier.size((cellSizeDp * 4.2f).dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size((cellSizeDp * 4).dp)) {
            val offsets = type.getOffsets(0)
            val color = theme.blockColors[type.colorIndex].copy(alpha = alpha)

            // Calculate bounding box to center inside Canvas
            val minX = offsets.minOf { it.first }
            val maxX = offsets.maxOf { it.first }
            val minY = offsets.minOf { it.second }
            val maxY = offsets.maxOf { it.second }

            val pieceWidth = (maxX - minX + 1)
            val pieceHeight = (maxY - minY + 1)

            val pxCellSize = size.width / 4.2f
            val startX = (size.width - (pieceWidth * pxCellSize)) / 2f - (minX * pxCellSize)
            val startY = (size.height - (pieceHeight * pxCellSize)) / 2f - (minY * pxCellSize)

            for (cell in offsets) {
                val cx = startX + (cell.first * pxCellSize)
                val cy = startY + (cell.second * pxCellSize)
                val pad = 1.5f
                val rw = pxCellSize - pad * 2
                val corner = CornerRadius(rw * 0.2f, rw * 0.2f)

                drawRoundRect(
                    color = color,
                    topLeft = Offset(cx + pad, cy + pad),
                    size = Size(rw, rw),
                    cornerRadius = corner,
                    style = Fill
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.35f * alpha),
                    topLeft = Offset(cx + pad, cy + pad),
                    size = Size(rw, rw),
                    cornerRadius = corner,
                    style = Stroke(width = 1.2f)
                )
            }
        }
    }
}

@Composable
fun HoldBox(
    holdPiece: TetrominoType?,
    canHold: Boolean,
    theme: GameThemeColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(theme.surface)
            .border(1.dp, theme.surfaceBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "HOLD",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.onSurfaceSecondary,
            letterSpacing = 1.sp
        )
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            if (holdPiece != null) {
                MiniPieceView(
                    type = holdPiece,
                    theme = theme,
                    alpha = if (canHold) 1f else 0.4f
                )
            }
        }
    }
}

@Composable
fun NextQueueBox(
    nextPieces: List<TetrominoType>,
    theme: GameThemeColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(theme.surface)
            .border(1.dp, theme.surfaceBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "NEXT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.onSurfaceSecondary,
            letterSpacing = 1.sp
        )

        // Show up to 4 upcoming pieces
        for (piece in nextPieces.take(4)) {
            MiniPieceView(
                type = piece,
                theme = theme,
                cellSizeDp = 11f
            )
        }
    }
}
