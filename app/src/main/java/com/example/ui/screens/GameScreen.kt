package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ControlMode
import com.example.game.GameViewModel
import com.example.model.GameMode
import com.example.ui.components.GameBoardCanvas
import com.example.ui.components.GameHeader
import com.example.ui.components.GameOverDialog
import com.example.ui.components.GamepadOverlay
import com.example.ui.components.HoldBox
import com.example.ui.components.NextQueueBox
import com.example.ui.components.PauseDialog
import com.example.ui.components.VictoryDialog
import com.example.ui.components.gameGestureControls
import com.example.ui.theme.ThemePalettes

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onOpenSettings: () -> Unit,
    onQuitToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val theme = ThemePalettes.getTheme(state.settings.theme)

    // Android hardware back button pauses game or exits pause
    BackHandler {
        if (!state.isPaused && !state.isGameOver && !state.isVictory) {
            viewModel.togglePause()
        } else if (state.isPaused) {
            viewModel.togglePause()
        } else {
            onQuitToMenu()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
            .testTag("game_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top HUD Header
            GameHeader(
                state = state,
                theme = theme,
                onPauseClick = { viewModel.togglePause() }
            )

            // 2. Main Playfield Area (Hold + Board + Next)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                // Left Column: Hold Box & Objective info
                Column(
                    modifier = Modifier
                        .weight(0.24f)
                        .padding(end = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HoldBox(
                        holdPiece = state.holdPiece,
                        canHold = state.canHold,
                        theme = theme,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.gameMode == GameMode.CHALLENGE && state.challengeStage != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.surface)
                                .border(1.dp, theme.surfaceBorder, RoundedCornerShape(8.dp))
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "GOAL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.accent
                            )
                            Text(
                                text = state.challengeStage!!.goalDescription,
                                fontSize = 9.sp,
                                color = theme.onSurfaceSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Center: 10x20 Game Matrix Canvas with Gestures
                Box(
                    modifier = Modifier
                        .weight(0.52f)
                        .gameGestureControls(
                            sensitivity = state.settings.touchSensitivity,
                            onMoveLeft = { viewModel.moveLeft() },
                            onMoveRight = { viewModel.moveRight() },
                            onSoftDrop = { viewModel.softDrop() },
                            onHardDrop = { viewModel.hardDrop() },
                            onRotate = { viewModel.rotate(clockwise = true) },
                            onHold = { viewModel.hold() }
                        )
                ) {
                    GameBoardCanvas(
                        board = viewModel.board,
                        currentPiece = state.currentPiece,
                        ghostPiece = state.ghostPiece,
                        particleManager = viewModel.particleManager,
                        theme = theme
                    )
                }

                // Right Column: Next Pieces Queue Box
                Column(
                    modifier = Modifier
                        .weight(0.24f)
                        .padding(start = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NextQueueBox(
                        nextPieces = state.nextPieces,
                        theme = theme,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 3. Bottom Controls Area (Overlay buttons if enabled)
            if (state.settings.controlMode != ControlMode.GESTURES_ONLY) {
                GamepadOverlay(
                    theme = theme,
                    buttonOpacity = state.settings.buttonOpacity,
                    onMoveLeft = { viewModel.moveLeft() },
                    onMoveRight = { viewModel.moveRight() },
                    onSoftDrop = { viewModel.softDrop() },
                    onHardDrop = { viewModel.hardDrop() },
                    onRotateCw = { viewModel.rotate(clockwise = true) },
                    onRotateCcw = { viewModel.rotate(clockwise = false) },
                    onHold = { viewModel.hold() }
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Overlay Dialogs
        if (state.isPaused) {
            PauseDialog(
                state = state,
                theme = theme,
                onResume = { viewModel.togglePause() },
                onRestart = { viewModel.startNewGame(state.gameMode, state.challengeStage?.stageNumber ?: 1) },
                onOpenSettings = onOpenSettings,
                onQuitToMenu = onQuitToMenu,
                onToggleSound = {
                    viewModel.updateSettings(state.settings.copy(isSoundEnabled = !state.settings.isSoundEnabled))
                },
                onToggleMusic = {
                    viewModel.updateSettings(state.settings.copy(isMusicEnabled = !state.settings.isMusicEnabled))
                    if (!state.settings.isMusicEnabled) viewModel.startMusic() else viewModel.stopMusic()
                }
            )
        }

        if (state.isGameOver) {
            GameOverDialog(
                state = state,
                theme = theme,
                onRestart = { viewModel.startNewGame(state.gameMode, state.challengeStage?.stageNumber ?: 1) },
                onQuitToMenu = onQuitToMenu
            )
        }

        if (state.isVictory) {
            VictoryDialog(
                state = state,
                theme = theme,
                onRestart = {
                    val nextStage = (state.challengeStage?.stageNumber ?: 1) + 1
                    viewModel.startNewGame(state.gameMode, nextStage)
                },
                onQuitToMenu = onQuitToMenu
            )
        }
    }
}
