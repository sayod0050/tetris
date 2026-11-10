package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameViewModel
import com.example.model.GameMode
import com.example.ui.theme.ThemePalettes
import kotlinx.coroutines.delay

enum class Screen {
    SPLASH,
    HOME,
    MODE_SELECT,
    GAME,
    STATS,
    SETTINGS,
    TUTORIAL,
    ABOUT
}

@Composable
fun AppNavigation(
    viewModel: GameViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    val state by viewModel.uiState.collectAsState()
    val theme = ThemePalettes.getTheme(state.settings.theme)

    // Back button handling
    if (currentScreen != Screen.HOME && currentScreen != Screen.SPLASH && currentScreen != Screen.GAME) {
        BackHandler {
            currentScreen = Screen.HOME
        }
    }

    Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
        when (screen) {
            Screen.SPLASH -> {
                SplashScreen(
                    theme = theme,
                    onSplashFinished = { currentScreen = Screen.HOME }
                )
            }
            Screen.HOME -> {
                HomeScreen(
                    theme = theme,
                    onQuickPlay = {
                        viewModel.startNewGame(GameMode.CLASSIC, 1)
                        currentScreen = Screen.GAME
                    },
                    onModeSelect = { currentScreen = Screen.MODE_SELECT },
                    onStats = { currentScreen = Screen.STATS },
                    onTutorial = { currentScreen = Screen.TUTORIAL },
                    onSettings = { currentScreen = Screen.SETTINGS },
                    onAbout = { currentScreen = Screen.ABOUT }
                )
            }
            Screen.MODE_SELECT -> {
                ModeSelectScreen(
                    theme = theme,
                    stats = state.stats,
                    onSelectMode = { mode, stage ->
                        viewModel.startNewGame(mode, stage)
                        currentScreen = Screen.GAME
                    },
                    onBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.GAME -> {
                GameScreen(
                    viewModel = viewModel,
                    onOpenSettings = { currentScreen = Screen.SETTINGS },
                    onQuitToMenu = {
                        viewModel.stopMusic()
                        currentScreen = Screen.HOME
                    }
                )
            }
            Screen.STATS -> {
                StatsScreen(
                    stats = state.stats,
                    theme = theme,
                    onResetStats = { viewModel.resetAllStats() },
                    onBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.SETTINGS -> {
                SettingsScreen(
                    settings = state.settings,
                    theme = theme,
                    onUpdateSettings = { viewModel.updateSettings(it) },
                    onBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.TUTORIAL -> {
                TutorialScreen(
                    theme = theme,
                    onBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.ABOUT -> {
                AboutScreen(
                    theme = theme,
                    onBack = { currentScreen = Screen.HOME }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(
    theme: com.example.ui.theme.GameThemeColors,
    onSplashFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1200) // Smooth brief intro
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(theme.background, theme.backgroundGradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "BLOCK DROP",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = theme.accent,
                letterSpacing = 4.sp
            )
            Text(
                text = "PREMIUM ANDROID PUZZLE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = theme.onSurfaceSecondary,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
