package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.HapticsManager
import com.example.audio.SoundEngine
import com.example.data.ControlMode
import com.example.data.GamePreferences
import com.example.data.GameStats
import com.example.data.UserSettings
import com.example.data.VisualTheme
import com.example.model.ChallengeObjective
import com.example.model.ChallengeStages
import com.example.model.GameMode
import com.example.model.Piece
import com.example.model.TetrominoType
import com.example.ui.theme.ThemePalettes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameUiState(
    val gameMode: GameMode = GameMode.CLASSIC,
    val challengeStage: ChallengeObjective? = null,
    val currentPiece: Piece? = null,
    val ghostPiece: Piece? = null,
    val holdPiece: TetrominoType? = null,
    val canHold: Boolean = true,
    val nextPieces: List<TetrominoType> = emptyList(),
    val score: Long = 0,
    val linesCleared: Int = 0,
    val level: Int = 1,
    val comboCount: Int = 0,
    val maxComboInGame: Int = 0,
    val quadsInGame: Int = 0,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val victoryMessage: String = "",
    val timeElapsedMs: Long = 0,
    val timeRemainingMs: Long = 0, // For Time Attack & timed challenges
    val isBackToBack: Boolean = false,
    val isNewHighScore: Boolean = false,
    val stats: GameStats = GameStats(),
    val settings: UserSettings = UserSettings()
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application)
    val soundEngine = SoundEngine()
    val haptics = HapticsManager(application)

    val board = Board()
    val randomizer = BagRandomizer()
    val particleManager = ParticleManager()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var lastFallTime = 0L
    private var lockStartTime = 0L
    private var lockResetCount = 0
    private val maxLockResets = 15
    private val lockDelayMs = 500L

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        val loadedSettings = preferences.loadSettings()
        val loadedStats = preferences.loadStats()

        soundEngine.isSoundEnabled = loadedSettings.isSoundEnabled
        soundEngine.isMusicEnabled = loadedSettings.isMusicEnabled
        soundEngine.soundVolume = loadedSettings.soundVolume
        soundEngine.musicVolume = loadedSettings.musicVolume
        haptics.isHapticsEnabled = loadedSettings.isHapticsEnabled

        _uiState.value = _uiState.value.copy(
            settings = loadedSettings,
            stats = loadedStats
        )
    }

    fun startMusic() {
        if (_uiState.value.settings.isMusicEnabled) {
            soundEngine.startBackgroundMusic()
        }
    }

    fun stopMusic() {
        soundEngine.stopBackgroundMusic()
    }

    fun startNewGame(mode: GameMode, stageNumber: Int = 1) {
        gameLoopJob?.cancel()
        board.clear()
        randomizer.reset()
        particleManager.clear()

        val stage = if (mode == GameMode.CHALLENGE) {
            ChallengeStages.STAGES.find { it.stageNumber == stageNumber } ?: ChallengeStages.STAGES.first()
        } else null

        if (stage != null && stage.startingGarbageLines > 0) {
            board.setupGarbageRows(stage.startingGarbageLines)
        }

        val startingLevel = stage?.startLevel ?: 1
        val initialTimeLimit = if (mode == GameMode.TIME_ATTACK) {
            120_000L
        } else if (stage != null && stage.timeLimitSec > 0) {
            stage.timeLimitSec * 1000L
        } else 0L

        val firstPieceType = randomizer.nextPiece()
        val initialPiece = Piece(firstPieceType)
        val initialGhost = if (_uiState.value.settings.isGhostPieceEnabled) {
            board.getGhostPiece(initialPiece)
        } else null

        _uiState.value = _uiState.value.copy(
            gameMode = mode,
            challengeStage = stage,
            currentPiece = initialPiece,
            ghostPiece = initialGhost,
            holdPiece = null,
            canHold = true,
            nextPieces = randomizer.peekQueue(5),
            score = 0,
            linesCleared = 0,
            level = startingLevel,
            comboCount = 0,
            maxComboInGame = 0,
            quadsInGame = 0,
            isPaused = false,
            isGameOver = false,
            isVictory = false,
            victoryMessage = "",
            timeElapsedMs = 0,
            timeRemainingMs = initialTimeLimit,
            isBackToBack = false,
            isNewHighScore = false
        )

        lastFallTime = System.currentTimeMillis()
        lockStartTime = 0L
        lockResetCount = 0

        startMusic()
        launchGameLoop()
    }

    private fun launchGameLoop() {
        gameLoopJob = viewModelScope.launch {
            var lastTickTime = System.currentTimeMillis()
            while (isActive) {
                delay(16) // ~60fps tick
                val now = System.currentTimeMillis()
                val delta = (now - lastTickTime).coerceAtMost(50L)
                lastTickTime = now

                val state = _uiState.value
                if (state.isPaused || state.isGameOver || state.isVictory) continue

                // Update timers
                val newElapsed = state.timeElapsedMs + delta
                var newRemaining = state.timeRemainingMs
                if (newRemaining > 0) {
                    newRemaining = (newRemaining - delta).coerceAtLeast(0L)
                    if (newRemaining <= 0L) {
                        handleTimeExpired()
                        continue
                    }
                }

                _uiState.value = _uiState.value.copy(
                    timeElapsedMs = newElapsed,
                    timeRemainingMs = newRemaining
                )

                // Update particle engine
                particleManager.update(delta / 1000f)

                // Piece gravity fall
                val currentPiece = state.currentPiece ?: continue
                val fallInterval = calculateFallInterval(state.level, state.gameMode)

                // Check if piece is resting on something
                val cannotFall = !board.isValidPosition(currentPiece.moved(0, 1))

                if (cannotFall) {
                    if (lockStartTime == 0L) {
                        lockStartTime = now
                    } else if (now - lockStartTime >= lockDelayMs) {
                        // Commit lock
                        lockCurrentPiece()
                    }
                } else {
                    lockStartTime = 0L
                    if (now - lastFallTime >= fallInterval) {
                        lastFallTime = now
                        val fallenPiece = currentPiece.moved(0, 1)
                        updatePiecePosition(fallenPiece, resetLock = false)
                    }
                }
            }
        }
    }

    private fun calculateFallInterval(level: Int, mode: GameMode): Long {
        if (mode == GameMode.ZEN) return 900L
        val lvl = level.coerceIn(1, 20)
        return maxOf(60L, (1000.0 * Math.pow(0.82, (lvl - 1).toDouble())).toLong())
    }

    // ==================== CONTROLS ====================

    fun moveLeft() {
        val state = _uiState.value
        if (state.isPaused || state.isGameOver || state.isVictory) return
        val current = state.currentPiece ?: return
        val moved = current.moved(-1, 0)
        if (board.isValidPosition(moved)) {
            soundEngine.playMove()
            haptics.tick()
            updatePiecePosition(moved, resetLock = true)
        }
    }

    fun moveRight() {
        val state = _uiState.value
        if (state.isPaused || state.isGameOver || state.isVictory) return
        val current = state.currentPiece ?: return
        val moved = current.moved(1, 0)
        if (board.isValidPosition(moved)) {
            soundEngine.playMove()
            haptics.tick()
            updatePiecePosition(moved, resetLock = true)
        }
    }

    fun rotate(clockwise: Boolean) {
        val state = _uiState.value
        if (state.isPaused || state.isGameOver || state.isVictory) return
        val current = state.currentPiece ?: return
        val rotated = board.tryRotate(current, clockwise)
        if (rotated != null) {
            soundEngine.playRotate()
            haptics.click()
            updatePiecePosition(rotated, resetLock = true)
        }
    }

    fun softDrop() {
        val state = _uiState.value
        if (state.isPaused || state.isGameOver || state.isVictory) return
        val current = state.currentPiece ?: return
        val fallen = current.moved(0, 1)
        if (board.isValidPosition(fallen)) {
            soundEngine.playSoftDrop()
            haptics.tick()
            _uiState.value = _uiState.value.copy(score = state.score + 1)
            updatePiecePosition(fallen, resetLock = false)
            lastFallTime = System.currentTimeMillis()
        } else {
            if (lockStartTime == 0L) {
                lockStartTime = System.currentTimeMillis()
            }
        }
    }

    fun hardDrop() {
        val state = _uiState.value
        if (state.isPaused || state.isGameOver || state.isVictory) return
        val current = state.currentPiece ?: return
        val ghost = board.getGhostPiece(current)
        val dropDistance = ghost.y - current.y
        _uiState.value = _uiState.value.copy(
            score = state.score + (dropDistance * 2L),
            currentPiece = ghost
        )
        soundEngine.playHardDrop()
        haptics.heavy()
        lockCurrentPiece()
    }

    fun hold() {
        val state = _uiState.value
        if (state.isPaused || state.isGameOver || state.isVictory || !state.canHold) return
        val current = state.currentPiece ?: return

        soundEngine.playHold()
        haptics.click()

        val nextHold = current.type
        val pieceToSpawn = if (state.holdPiece != null) {
            Piece(state.holdPiece)
        } else {
            Piece(randomizer.nextPiece())
        }

        val ghost = if (state.settings.isGhostPieceEnabled) board.getGhostPiece(pieceToSpawn) else null

        _uiState.value = _uiState.value.copy(
            holdPiece = nextHold,
            canHold = false,
            currentPiece = pieceToSpawn,
            ghostPiece = ghost,
            nextPieces = randomizer.peekQueue(5)
        )
        lockStartTime = 0L
        lockResetCount = 0
        lastFallTime = System.currentTimeMillis()
    }

    private fun updatePiecePosition(piece: Piece, resetLock: Boolean) {
        if (resetLock && lockStartTime != 0L && lockResetCount < maxLockResets) {
            lockStartTime = System.currentTimeMillis()
            lockResetCount++
        }
        val ghost = if (_uiState.value.settings.isGhostPieceEnabled) board.getGhostPiece(piece) else null
        _uiState.value = _uiState.value.copy(
            currentPiece = piece,
            ghostPiece = ghost
        )
    }

    private fun lockCurrentPiece() {
        val current = _uiState.value.currentPiece ?: return
        val toppedOut = board.lockPiece(current)

        if (toppedOut) {
            handleGameOver()
            return
        }

        // Check line clears
        val clearedRows = board.clearLines()
        val numCleared = clearedRows.size

        var newScore = _uiState.value.score
        var newLines = _uiState.value.linesCleared + numCleared
        var newCombo = _uiState.value.comboCount
        var newMaxCombo = _uiState.value.maxComboInGame
        var newQuads = _uiState.value.quadsInGame
        var newB2B = _uiState.value.isBackToBack
        val currentLevel = _uiState.value.level
        val themeColors = ThemePalettes.getTheme(_uiState.value.settings.theme)

        if (numCleared > 0) {
            newCombo++
            if (newCombo > newMaxCombo) newMaxCombo = newCombo

            // Trigger particles for each cleared row
            for (row in clearedRows) {
                particleManager.spawnLineClearParticles(
                    rowY = row.toFloat(),
                    boardWidth = 10f,
                    cellHeight = 1f,
                    colors = themeColors.blockColors
                )
            }

            // Line clear points
            val basePoints = when (numCleared) {
                1 -> {
                    soundEngine.playLineClear()
                    100L * currentLevel
                }
                2 -> {
                    soundEngine.playLineClear()
                    300L * currentLevel
                }
                3 -> {
                    soundEngine.playLineClear()
                    500L * currentLevel
                }
                4 -> {
                    newQuads++
                    val isB2bHit = newB2B
                    newB2B = true
                    soundEngine.playQuadClear()
                    particleManager.spawnBanner(if (isB2bHit) "B2B QUAD!" else "QUAD!", themeColors.accent, 8f)
                    if (isB2bHit) 1200L * currentLevel else 800L * currentLevel
                }
                else -> 0L
            }

            if (numCleared < 4) {
                newB2B = false
            }

            // Combo bonus
            val comboBonus = if (newCombo > 1) {
                soundEngine.playCombo(newCombo)
                particleManager.spawnBanner("COMBO x$newCombo", themeColors.accent, 10f)
                50L * newCombo * currentLevel
            } else 0L

            newScore += basePoints + comboBonus
            haptics.doubleClick()
        } else {
            newCombo = 0
        }

        // Check Level progression (every 10 lines in Classic mode)
        val calculatedLevel = if (_uiState.value.gameMode == GameMode.CLASSIC) {
            1 + (newLines / 10)
        } else currentLevel

        if (calculatedLevel > currentLevel) {
            soundEngine.playLevelUp()
            particleManager.spawnBanner("LEVEL UP! L$calculatedLevel", themeColors.accent, 6f)
        }

        // Check Victory / Objectives
        checkModeObjectives(newScore, newLines, newQuads, newMaxCombo)

        if (_uiState.value.isVictory || _uiState.value.isGameOver) return

        // Spawn next piece
        val nextType = randomizer.nextPiece()
        val nextPiece = Piece(nextType)

        if (!board.isValidPosition(nextPiece)) {
            handleGameOver()
            return
        }

        val nextGhost = if (_uiState.value.settings.isGhostPieceEnabled) board.getGhostPiece(nextPiece) else null

        _uiState.value = _uiState.value.copy(
            currentPiece = nextPiece,
            ghostPiece = nextGhost,
            canHold = true,
            nextPieces = randomizer.peekQueue(5),
            score = newScore,
            linesCleared = newLines,
            level = calculatedLevel,
            comboCount = newCombo,
            maxComboInGame = newMaxCombo,
            quadsInGame = newQuads,
            isBackToBack = newB2B
        )

        lockStartTime = 0L
        lockResetCount = 0
        lastFallTime = System.currentTimeMillis()
    }

    private fun checkModeObjectives(score: Long, lines: Int, quads: Int, maxCombo: Int) {
        val mode = _uiState.value.gameMode
        when (mode) {
            GameMode.SPRINT -> {
                if (lines >= 40) {
                    val time = _uiState.value.timeElapsedMs
                    preferences.updateSprintTime(time)
                    preferences.recordGameFinished(lines, _uiState.value.level, maxCombo, quads)
                    soundEngine.playLevelUp()
                    _uiState.value = _uiState.value.copy(
                        isVictory = true,
                        victoryMessage = "40 Lines Cleared in ${formatTime(time)}!",
                        stats = preferences.loadStats()
                    )
                }
            }
            GameMode.CHALLENGE -> {
                val stage = _uiState.value.challengeStage ?: return
                var completed = false
                if (stage.targetLines > 0 && lines >= stage.targetLines) completed = true
                if (stage.targetScore > 0 && score >= stage.targetScore) completed = true
                if (stage.targetQuads > 0 && quads >= stage.targetQuads) completed = true
                if (stage.targetCombo > 0 && maxCombo >= stage.targetCombo) completed = true

                if (completed) {
                    preferences.markStageCompleted(stage.stageNumber)
                    preferences.recordGameFinished(lines, _uiState.value.level, maxCombo, quads)
                    soundEngine.playLevelUp()
                    _uiState.value = _uiState.value.copy(
                        isVictory = true,
                        victoryMessage = "Stage ${stage.stageNumber} Completed!",
                        stats = preferences.loadStats()
                    )
                }
            }
            else -> {}
        }
    }

    private fun handleTimeExpired() {
        val mode = _uiState.value.gameMode
        if (mode == GameMode.TIME_ATTACK) {
            val score = _uiState.value.score
            val isNew = score > _uiState.value.stats.timeAttackHighScore
            preferences.updateTimeAttackScore(score)
            preferences.recordGameFinished(
                _uiState.value.linesCleared,
                _uiState.value.level,
                _uiState.value.maxComboInGame,
                _uiState.value.quadsInGame
            )
            soundEngine.playLevelUp()
            _uiState.value = _uiState.value.copy(
                isVictory = true,
                isNewHighScore = isNew,
                victoryMessage = "Time's Up! Final Score: $score",
                stats = preferences.loadStats()
            )
        } else if (mode == GameMode.CHALLENGE) {
            handleGameOver()
        }
    }

    private fun handleGameOver() {
        soundEngine.playGameOver()
        haptics.heavy()

        val score = _uiState.value.score
        var isNewHigh = false

        when (_uiState.value.gameMode) {
            GameMode.CLASSIC -> {
                isNewHigh = score > _uiState.value.stats.classicHighScore
                preferences.updateClassicScore(score)
            }
            GameMode.ZEN -> {
                isNewHigh = score > _uiState.value.stats.zenHighScore
                preferences.updateZenScore(score)
            }
            else -> {}
        }

        preferences.recordGameFinished(
            _uiState.value.linesCleared,
            _uiState.value.level,
            _uiState.value.maxComboInGame,
            _uiState.value.quadsInGame
        )

        _uiState.value = _uiState.value.copy(
            isGameOver = true,
            isNewHighScore = isNewHigh,
            stats = preferences.loadStats()
        )
    }

    fun togglePause() {
        val paused = !_uiState.value.isPaused
        _uiState.value = _uiState.value.copy(isPaused = paused)
        if (paused) {
            soundEngine.stopBackgroundMusic()
        } else {
            startMusic()
        }
    }

    fun updateSettings(newSettings: UserSettings) {
        preferences.saveSettings(newSettings)
        soundEngine.isSoundEnabled = newSettings.isSoundEnabled
        soundEngine.isMusicEnabled = newSettings.isMusicEnabled
        soundEngine.soundVolume = newSettings.soundVolume
        soundEngine.musicVolume = newSettings.musicVolume
        haptics.isHapticsEnabled = newSettings.isHapticsEnabled

        _uiState.value = _uiState.value.copy(
            settings = newSettings,
            ghostPiece = if (newSettings.isGhostPieceEnabled && _uiState.value.currentPiece != null) {
                board.getGhostPiece(_uiState.value.currentPiece!!)
            } else null
        )
    }

    fun resetAllStats() {
        preferences.resetStats()
        _uiState.value = _uiState.value.copy(stats = preferences.loadStats())
    }

    private fun formatTime(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 60000)
        val tenths = (ms % 1000) / 100
        return "%02d:%02d.%d".format(minutes, seconds, tenths)
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        soundEngine.stopBackgroundMusic()
    }
}
