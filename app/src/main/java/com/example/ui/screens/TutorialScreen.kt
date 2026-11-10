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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TetrominoType
import com.example.ui.components.MiniPieceView
import com.example.ui.theme.GameThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialScreen(
    theme: GameThemeColors,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = theme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "HOW TO PLAY",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("tutorial_back_button")) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Core Rules
            item {
                TutorialCard(
                    title = "THE OBJECTIVE",
                    content = "Blocks fall from the top of the 10x20 grid. Guide, rotate, and drop them to form solid horizontal lines without any gaps. When a row is completely filled, it clears and awards points. Don't let blocks stack up to the top!",
                    theme = theme
                )
            }

            // The 7 Pieces
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.surface)
                        .border(1.dp, theme.surfaceBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "THE 7 BLOCK SHAPES",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.accent,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Every set of 7 pieces is fairly randomized using the 7-Bag algorithm.",
                        fontSize = 12.sp,
                        color = theme.onSurfaceSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (type in TetrominoType.values()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                MiniPieceView(type = type, theme = theme, cellSizeDp = 9f)
                                Text(
                                    text = type.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.onSurface,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Controls
            item {
                TutorialCard(
                    title = "CONTROLS & GESTURES",
                    content = "• Swipe Left / Right: Move piece horizontally\n• Swipe Down: Soft drop (accelerated fall)\n• Double Tap or Swipe Down Flick: Hard drop (instant lock)\n• Single Tap: Rotate Clockwise\n• Swipe Up: Store piece into the HOLD buffer\n\nOn-screen buttons are also provided and can be customized in Settings.",
                    theme = theme
                )
            }

            // Wall Kicks & Lock Delay
            item {
                TutorialCard(
                    title = "SRS WALL KICKS & LOCK DELAY",
                    content = "• Super Rotation System (SRS): Allows pieces to kick away from boundaries or obstacles when rotated in tight spots.\n• Lock Delay: When a piece lands, you have a 500ms grace window to slide or rotate it before it locks firmly into the grid.",
                    theme = theme
                )
            }

            // Scoring Table
            item {
                TutorialCard(
                    title = "SCORING & COMBOS",
                    content = "• Single (1 Line): 100 × Level\n• Double (2 Lines): 300 × Level\n• Triple (3 Lines): 500 × Level\n• Quad (4 Lines): 800 × Level (1,200 × Level on Back-to-Back!)\n• Combos: Consecutive clears trigger combo multipliers (+50 × Combo × Level)!\n• Soft Drop: +1 pt per cell\n• Hard Drop: +2 pts per cell",
                    theme = theme
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TutorialCard(
    title: String,
    content: String,
    theme: GameThemeColors
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(theme.surface)
            .border(1.dp, theme.surfaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = theme.accent,
            letterSpacing = 1.sp
        )
        Text(
            text = content,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = theme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
