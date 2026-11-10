package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.GameStats
import com.example.ui.theme.GameThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    stats: GameStats,
    theme: GameThemeColors,
    onResetStats: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "STATISTICS & RECORDS",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("stats_back_button")) {
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

            // High Scores Section
            item {
                Text(
                    text = "PERSONAL BESTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.accent,
                    letterSpacing = 1.2.sp
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.surface)
                        .border(1.dp, theme.surfaceBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatRow("Classic High Score", "${stats.classicHighScore} pts", theme)

                    val sprintText = if (stats.sprintBestTimeMs > 0) {
                        val sec = (stats.sprintBestTimeMs / 1000) % 60
                        val min = (stats.sprintBestTimeMs / 60000)
                        val tenths = (stats.sprintBestTimeMs % 1000) / 100
                        "%02d:%02d.%d".format(min, sec, tenths)
                    } else "Not yet cleared"
                    StatRow("Sprint 40L Best Time", sprintText, theme)

                    StatRow("Time Attack Best", "${stats.timeAttackHighScore} pts", theme)
                    StatRow("Zen Mode Best", "${stats.zenHighScore} pts", theme)
                    StatRow("Challenge Stages Cleared", "${stats.completedStages.size} / 10", theme)
                }
            }

            // Career Lifetime Stats
            item {
                Text(
                    text = "LIFETIME CAREER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.accent,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.surface)
                        .border(1.dp, theme.surfaceBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatRow("Total Games Played", stats.totalGamesPlayed.toString(), theme)
                    StatRow("Total Lines Cleared", stats.totalLinesCleared.toString(), theme)
                    StatRow("Highest Level Reached", stats.highestLevel.toString(), theme)
                    StatRow("Longest Combo Chain", "x${stats.longestCombo}", theme)
                    StatRow("Total Quads Dropped", stats.totalQuads.toString(), theme)
                }
            }

            // Reset Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reset_stats_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = theme.blockColors[4],
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "RESET ALL STATISTICS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.blockColors[4],
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset All Statistics?", fontWeight = FontWeight.Bold) },
            text = { Text("This will clear all personal best records, career stats, and challenge stage progress.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetConfirm = false
                        onResetStats()
                    }
                ) {
                    Text("RESET", color = theme.blockColors[4], fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
private fun StatRow(label: String, value: String, theme: GameThemeColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = theme.onSurfaceSecondary)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
    }
}
