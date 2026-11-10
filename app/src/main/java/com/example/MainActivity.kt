package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.game.GameViewModel
import com.example.ui.screens.AppNavigation
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    AppNavigation(viewModel = gameViewModel)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop music and pause if playing
        gameViewModel.stopMusic()
        val state = gameViewModel.uiState.value
        if (!state.isPaused && !state.isGameOver && !state.isVictory && state.currentPiece != null) {
            gameViewModel.togglePause()
        }
    }

    override fun onResume() {
        super.onResume()
        val state = gameViewModel.uiState.value
        if (!state.isPaused && !state.isGameOver && !state.isVictory && state.settings.isMusicEnabled) {
            gameViewModel.startMusic()
        }
    }
}
