package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GameStats
import com.example.model.ChallengeObjective
import com.example.model.ChallengeStages
import com.example.model.GameMode
import com.example.ui.theme.GameThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelectScreen(
    theme: GameThemeColors,
    stats: GameStats,
    onSelectMode: (GameMode, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showChallengeStagePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SELECT GAME MODE",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("mode_back_button")) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 1. Classic Mode
            item {
                ModeCard(
                    mode = GameMode.CLASSIC,
                    accentColor = theme.blockColors[0],
                    theme = theme,
                    highScoreText = if (stats.classicHighScore > 0) "Best: ${stats.classicHighScore} pts" else null,
                    onClick = { onSelectMode(GameMode.CLASSIC, 1) }
                )
            }

            // 2. Sprint Mode (40 Lines)
            item {
                val bestTimeFormatted = if (stats.sprintBestTimeMs > 0) {
                    val sec = (stats.sprintBestTimeMs / 1000) % 60
                    val min = (stats.sprintBestTimeMs / 60000)
                    val tenths = (stats.sprintBestTimeMs % 1000) / 100
                    "Record: %02d:%02d.%d".format(min, sec, tenths)
                } else null

                ModeCard(
                    mode = GameMode.SPRINT,
                    accentColor = theme.blockColors[3],
                    theme = theme,
                    highScoreText = bestTimeFormatted,
                    onClick = { onSelectMode(GameMode.SPRINT, 1) }
                )
            }

            // 3. Time Attack (120s)
            item {
                ModeCard(
                    mode = GameMode.TIME_ATTACK,
                    accentColor = theme.blockColors[4],
                    theme = theme,
                    highScoreText = if (stats.timeAttackHighScore > 0) "Best: ${stats.timeAttackHighScore} pts" else null,
                    onClick = { onSelectMode(GameMode.TIME_ATTACK, 1) }
                )
            }

            // 4. Zen Mode
            item {
                ModeCard(
                    mode = GameMode.ZEN,
                    accentColor = theme.blockColors[2],
                    theme = theme,
                    highScoreText = if (stats.zenHighScore > 0) "Best: ${stats.zenHighScore} pts" else null,
                    onClick = { onSelectMode(GameMode.ZEN, 1) }
                )
            }

            // 5. Challenge Mode
            item {
                val completedCount = stats.completedStages.size
                ModeCard(
                    mode = GameMode.CHALLENGE,
                    accentColor = theme.blockColors[6],
                    theme = theme,
                    highScoreText = "$completedCount/10 Stages Cleared",
                    onClick = { showChallengeStagePicker = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showChallengeStagePicker) {
        ChallengeStageDialog(
            stages = ChallengeStages.STAGES,
            completedStages = stats.completedStages,
            theme = theme,
            onSelectStage = { stageNum ->
                showChallengeStagePicker = false
                onSelectMode(GameMode.CHALLENGE, stageNum)
            },
            onDismiss = { showChallengeStagePicker = false }
        )
    }
}

@Composable
private fun ModeCard(
    mode: GameMode,
    accentColor: androidx.compose.ui.graphics.Color,
    theme: GameThemeColors,
    highScoreText: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(theme.surface)
            .border(1.5.dp, theme.surfaceBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("mode_card_${mode.name.lowercase()}")
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Text(
                        text = mode.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.onSurface,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Text(
                        text = " • ${mode.subtitle}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    text = mode.description,
                    fontSize = 12.sp,
                    color = theme.onSurfaceSecondary,
                    modifier = Modifier.padding(top = 4.dp, end = 8.dp)
                )

                if (highScoreText != null) {
                    Text(
                        text = highScoreText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accent,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start ${mode.title}",
                    tint = accentColor
                )
            }
        }
    }
}

@Composable
fun ChallengeStageDialog(
    stages: List<ChallengeObjective>,
    completedStages: Set<Int>,
    theme: GameThemeColors,
    onSelectStage: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(theme.surface)
                .border(2.dp, theme.surfaceBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "SELECT STAGE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = theme.accent,
                letterSpacing = 1.sp
            )
            Text(
                text = "Complete objectives to unlock mastery badges.",
                fontSize = 12.sp,
                color = theme.onSurfaceSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stages) { stage ->
                    val isDone = completedStages.contains(stage.stageNumber)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDone) theme.accent.copy(alpha = 0.12f) else theme.background)
                            .border(1.dp, if (isDone) theme.accent.copy(alpha = 0.5f) else theme.surfaceBorder, RoundedCornerShape(12.dp))
                            .clickable { onSelectStage(stage.stageNumber) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Stage ${stage.stageNumber}: ${stage.title}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.onSurface
                                )
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Cleared",
                                        tint = theme.blockColors[1],
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(start = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = stage.goalDescription,
                                fontSize = 11.sp,
                                color = theme.onSurfaceSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Icon(
                            imageVector = if (isDone) Icons.Default.Check else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isDone) theme.accent else theme.onSurfaceSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
