package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.GameUiState
import com.example.ui.theme.GameThemeColors

@Composable
fun PauseDialog(
    state: GameUiState,
    theme: GameThemeColors,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onOpenSettings: () -> Unit,
    onQuitToMenu: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit
) {
    Dialog(
        onDismissRequest = onResume,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(theme.surface)
                .border(2.dp, theme.surfaceBorder, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PAUSED",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = theme.accent,
                letterSpacing = 2.sp
            )

            // Audio quick toggles
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (state.settings.isSoundEnabled) theme.accent.copy(alpha = 0.2f) else theme.surfaceBorder)
                        .clickable(onClick = onToggleSound),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.settings.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Toggle SFX",
                        tint = if (state.settings.isSoundEnabled) theme.accent else theme.onSurfaceSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (state.settings.isMusicEnabled) theme.accent.copy(alpha = 0.2f) else theme.surfaceBorder)
                        .clickable(onClick = onToggleMusic),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.settings.isMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                        contentDescription = "Toggle Music",
                        tint = if (state.settings.isMusicEnabled) theme.accent else theme.onSurfaceSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Button(
                onClick = onResume,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("resume_button"),
                colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = theme.background)
                Text("RESUME", fontWeight = FontWeight.Bold, color = theme.background, modifier = Modifier.padding(start = 8.dp))
            }

            OutlinedButton(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("dialog_restart_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = theme.onSurface)
                Text("RESTART", fontWeight = FontWeight.Bold, color = theme.onSurface, modifier = Modifier.padding(start = 8.dp))
            }

            OutlinedButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("dialog_settings_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = theme.onSurface)
                Text("SETTINGS", fontWeight = FontWeight.Bold, color = theme.onSurface, modifier = Modifier.padding(start = 8.dp))
            }

            OutlinedButton(
                onClick = onQuitToMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("dialog_menu_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = theme.onSurfaceSecondary)
                Text("MAIN MENU", fontWeight = FontWeight.Bold, color = theme.onSurfaceSecondary, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun GameOverDialog(
    state: GameUiState,
    theme: GameThemeColors,
    onRestart: () -> Unit,
    onQuitToMenu: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(theme.surface)
                .border(2.dp, theme.surfaceBorder, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "GAME OVER",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = theme.blockColors[4], // Laser red
                letterSpacing = 2.sp
            )

            if (state.isNewHighScore) {
                Text(
                    text = "★ NEW HIGH SCORE! ★",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.blockColors[1] // Gold
                )
            }

            // Stats Summary Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.background)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResultRow("Final Score", state.score.toString(), theme)
                ResultRow("Lines Cleared", state.linesCleared.toString(), theme)
                ResultRow("Level Reached", state.level.toString(), theme)
                ResultRow("Max Combo", "x${state.maxComboInGame}", theme)
                ResultRow("Quads Performed", state.quadsInGame.toString(), theme)
            }

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("game_over_retry_button"),
                colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = theme.background)
                Text("PLAY AGAIN", fontWeight = FontWeight.Bold, color = theme.background, modifier = Modifier.padding(start = 8.dp))
            }

            OutlinedButton(
                onClick = onQuitToMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("game_over_menu_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = theme.onSurface)
                Text("MAIN MENU", fontWeight = FontWeight.Bold, color = theme.onSurface, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun VictoryDialog(
    state: GameUiState,
    theme: GameThemeColors,
    onRestart: () -> Unit,
    onQuitToMenu: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(theme.surface)
                .border(2.dp, theme.accent, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "STAGE CLEARED!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = theme.accent,
                letterSpacing = 2.sp
            )

            Text(
                text = state.victoryMessage,
                fontSize = 14.sp,
                color = theme.onSurface,
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.background)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResultRow("Score", state.score.toString(), theme)
                ResultRow("Lines", state.linesCleared.toString(), theme)
                ResultRow("Max Combo", "x${state.maxComboInGame}", theme)
            }

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("victory_continue_button"),
                colors = ButtonDefaults.buttonColors(containerColor = theme.accent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CONTINUE", fontWeight = FontWeight.Bold, color = theme.background)
            }

            OutlinedButton(
                onClick = onQuitToMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("victory_menu_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("MAIN MENU", fontWeight = FontWeight.Bold, color = theme.onSurface)
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, theme: GameThemeColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = theme.onSurfaceSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
    }
}
