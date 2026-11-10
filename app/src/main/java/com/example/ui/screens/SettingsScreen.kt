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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.ControlMode
import com.example.data.UserSettings
import com.example.data.VisualTheme
import com.example.ui.theme.GameThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: UserSettings,
    theme: GameThemeColors,
    onUpdateSettings: (UserSettings) -> Unit,
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
                        "SETTINGS",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
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

            // Visual Theme Section
            item {
                Text(
                    text = "VISUAL THEME",
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
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (t in VisualTheme.values()) {
                        val isSelected = settings.theme == t
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) theme.accent.copy(alpha = 0.15f) else theme.background)
                                .border(1.dp, if (isSelected) theme.accent else theme.surfaceBorder, RoundedCornerShape(10.dp))
                                .clickable { onUpdateSettings(settings.copy(theme = t)) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = t.displayName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = theme.onSurface
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = theme.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Audio Controls
            item {
                Text(
                    text = "AUDIO & SOUND",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sound FX Toggle & Volume
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sound Effects", fontSize = 14.sp, color = theme.onSurface)
                        Switch(
                            checked = settings.isSoundEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(isSoundEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = theme.accent)
                        )
                    }

                    if (settings.isSoundEnabled) {
                        Text(
                            text = "SFX Volume (${(settings.soundVolume * 100).toInt()}%)",
                            fontSize = 12.sp,
                            color = theme.onSurfaceSecondary
                        )
                        Slider(
                            value = settings.soundVolume,
                            onValueChange = { onUpdateSettings(settings.copy(soundVolume = it)) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(thumbColor = theme.accent, activeTrackColor = theme.accent)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Music Toggle & Volume
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Background Music", fontSize = 14.sp, color = theme.onSurface)
                        Switch(
                            checked = settings.isMusicEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(isMusicEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = theme.accent)
                        )
                    }

                    if (settings.isMusicEnabled) {
                        Text(
                            text = "Music Volume (${(settings.musicVolume * 100).toInt()}%)",
                            fontSize = 12.sp,
                            color = theme.onSurfaceSecondary
                        )
                        Slider(
                            value = settings.musicVolume,
                            onValueChange = { onUpdateSettings(settings.copy(musicVolume = it)) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(thumbColor = theme.accent, activeTrackColor = theme.accent)
                        )
                    }
                }
            }

            // Controls & Haptics
            item {
                Text(
                    text = "CONTROLS & HAPTICS",
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Control Mode
                    Text("Control Type", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = theme.onSurface)
                    for (mode in ControlMode.values()) {
                        val isModeSelected = settings.controlMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isModeSelected) theme.accent.copy(alpha = 0.15f) else theme.background)
                                .clickable { onUpdateSettings(settings.copy(controlMode = mode)) }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(mode.displayName, fontSize = 13.sp, color = theme.onSurface)
                            if (isModeSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = theme.accent, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Haptics toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Vibration Haptics", fontSize = 14.sp, color = theme.onSurface)
                        Switch(
                            checked = settings.isHapticsEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(isHapticsEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = theme.accent)
                        )
                    }

                    // Ghost piece toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ghost Piece Projection", fontSize = 14.sp, color = theme.onSurface)
                        Switch(
                            checked = settings.isGhostPieceEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(isGhostPieceEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = theme.accent)
                        )
                    }

                    // Button Opacity
                    if (settings.controlMode != ControlMode.GESTURES_ONLY) {
                        Text(
                            text = "Button Opacity (${(settings.buttonOpacity * 100).toInt()}%)",
                            fontSize = 12.sp,
                            color = theme.onSurfaceSecondary
                        )
                        Slider(
                            value = settings.buttonOpacity,
                            onValueChange = { onUpdateSettings(settings.copy(buttonOpacity = it)) },
                            valueRange = 0.3f..1f,
                            colors = SliderDefaults.colors(thumbColor = theme.accent, activeTrackColor = theme.accent)
                        )
                    }

                    // Touch Sensitivity
                    Text(
                        text = "Swipe Sensitivity (%.1fx)".format(settings.touchSensitivity),
                        fontSize = 12.sp,
                        color = theme.onSurfaceSecondary
                    )
                    Slider(
                        value = settings.touchSensitivity,
                        onValueChange = { onUpdateSettings(settings.copy(touchSensitivity = it)) },
                        valueRange = 0.5f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = theme.accent, activeTrackColor = theme.accent)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
