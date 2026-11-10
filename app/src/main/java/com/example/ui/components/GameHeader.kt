package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameUiState
import com.example.model.GameMode
import com.example.ui.theme.GameThemeColors

@Composable
fun GameHeader(
    state: GameUiState,
    theme: GameThemeColors,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode & Timer
        Column {
            Text(
                text = state.gameMode.title.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = theme.accent,
                letterSpacing = 1.2.sp
            )

            val timerText = if (state.gameMode == GameMode.TIME_ATTACK || (state.timeRemainingMs > 0)) {
                formatCountdown(state.timeRemainingMs)
            } else {
                formatStopwatch(state.timeElapsedMs)
            }

            Text(
                text = timerText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = theme.onSurface
            )
        }

        // Stats Badges: Score, Lines, Level
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBadge(
                label = "SCORE",
                value = state.score.toString(),
                theme = theme
            )

            StatBadge(
                label = "LINES",
                value = if (state.gameMode == GameMode.SPRINT) "${state.linesCleared}/40" else state.linesCleared.toString(),
                theme = theme
            )

            if (state.gameMode != GameMode.ZEN) {
                StatBadge(
                    label = "LVL",
                    value = state.level.toString(),
                    theme = theme
                )
            }
        }

        // Pause Button
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(theme.surface)
                .border(1.dp, theme.surfaceBorder, CircleShape)
                .clickable(onClick = onPauseClick)
                .testTag("pause_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause Game",
                tint = theme.onSurface,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun StatBadge(
    label: String,
    value: String,
    theme: GameThemeColors
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(theme.surface)
            .border(1.dp, theme.surfaceBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = theme.onSurfaceSecondary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = theme.onSurface
        )
    }
}

private fun formatStopwatch(ms: Long): String {
    val sec = (ms / 1000) % 60
    val min = (ms / 60000)
    val tenths = (ms % 1000) / 100
    return "%02d:%02d.%d".format(min, sec, tenths)
}

private fun formatCountdown(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val sec = totalSec % 60
    val min = totalSec / 60
    return "%02d:%02d".format(min, sec)
}
