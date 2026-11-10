package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.ui.theme.GameThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
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
                        "ABOUT & CREDITS",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("about_back_button")) {
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

            // Game Identity Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.surface)
                        .border(1.dp, theme.surfaceBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BLOCK DROP",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.accent,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Version 1.0 • Release Ready",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.onSurfaceSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Architecture & Technology
            item {
                AboutInfoCard(
                    title = "ENGINE & ARCHITECTURE",
                    content = "Built natively for Android with Kotlin, Jetpack Compose, and Hardware-Accelerated 2D Canvas.\n\nTechnology Comparison Note:\nWhile Python + Kivy was historically considered for rapid prototypes, modern Google Play release requirements (API 34/36, 64-bit binaries, App Bundle .aab size constraints, zero-latency touch response, and strict background battery optimization) are best satisfied by pure native Android. Native Kotlin delivers silky 60+ FPS, instantaneous touch response, low battery usage, and full Google Play compliance without third-party wrapper overhead.",
                    theme = theme
                )
            }

            // Privacy Policy & Data Safety
            item {
                AboutInfoCard(
                    title = "PRIVACY & DATA SAFETY",
                    content = "• 100% Offline Gameplay: No internet connection required.\n• Zero Data Collection: Block Drop does not collect, transmit, or share any personal data, identifiers, analytics, or telemetry.\n• Local Storage Only: All scores and user settings are stored securely on your local device.\n• No Ads or Tracking: Focused entirely on delivering a pure, high-performance gameplay experience.",
                    theme = theme
                )
            }

            // Intellectual Property & Originality
            item {
                AboutInfoCard(
                    title = "ORIGINALITY & COMPLIANCE",
                    content = "Block Drop is an independent falling-block puzzle game featuring original artwork, procedural audio synthesis, custom color palettes, and unique game modes. It does not use copyrighted brand marks, trademarked logos, or proprietary audio assets.",
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
private fun AboutInfoCard(
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
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = theme.accent,
            letterSpacing = 1.sp
        )
        Text(
            text = content,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = theme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
