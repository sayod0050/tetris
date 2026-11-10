package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class VisualTheme(val displayName: String) {
    NEON_CYBER("Neon Cyber"),
    MINIMALIST_LIGHT("Minimalist Light"),
    MIDNIGHT_OLED("Midnight OLED"),
    RETRO_ARCADE("Retro Arcade"),
    SOLAR_SUNSET("Solar Sunset")
}

enum class ControlMode(val displayName: String) {
    HYBRID("Hybrid (Buttons + Gestures)"),
    BUTTONS_ONLY("Touch Buttons"),
    GESTURES_ONLY("Gestures Only")
}

data class GameStats(
    val classicHighScore: Long = 0,
    val sprintBestTimeMs: Long = 0, // 0 means uncompleted
    val timeAttackHighScore: Long = 0,
    val zenHighScore: Long = 0,
    val totalGamesPlayed: Int = 0,
    val totalLinesCleared: Long = 0,
    val highestLevel: Int = 1,
    val longestCombo: Int = 0,
    val totalQuads: Int = 0,
    val completedStages: Set<Int> = emptySet()
)

data class UserSettings(
    val theme: VisualTheme = VisualTheme.NEON_CYBER,
    val isSoundEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val soundVolume: Float = 0.8f,
    val musicVolume: Float = 0.5f,
    val isHapticsEnabled: Boolean = true,
    val isGhostPieceEnabled: Boolean = true,
    val controlMode: ControlMode = ControlMode.HYBRID,
    val buttonOpacity: Float = 0.85f,
    val touchSensitivity: Float = 1.0f
)

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("block_drop_prefs", Context.MODE_PRIVATE)

    fun loadSettings(): UserSettings {
        val themeName = prefs.getString("setting_theme", VisualTheme.NEON_CYBER.name) ?: VisualTheme.NEON_CYBER.name
        val theme = try { VisualTheme.valueOf(themeName) } catch (_: Exception) { VisualTheme.NEON_CYBER }

        val controlModeName = prefs.getString("setting_control_mode", ControlMode.HYBRID.name) ?: ControlMode.HYBRID.name
        val controlMode = try { ControlMode.valueOf(controlModeName) } catch (_: Exception) { ControlMode.HYBRID }

        return UserSettings(
            theme = theme,
            isSoundEnabled = prefs.getBoolean("setting_sound_enabled", true),
            isMusicEnabled = prefs.getBoolean("setting_music_enabled", true),
            soundVolume = prefs.getFloat("setting_sound_vol", 0.8f),
            musicVolume = prefs.getFloat("setting_music_vol", 0.5f),
            isHapticsEnabled = prefs.getBoolean("setting_haptics_enabled", true),
            isGhostPieceEnabled = prefs.getBoolean("setting_ghost_enabled", true),
            controlMode = controlMode,
            buttonOpacity = prefs.getFloat("setting_button_opacity", 0.85f),
            touchSensitivity = prefs.getFloat("setting_sensitivity", 1.0f)
        )
    }

    fun saveSettings(settings: UserSettings) {
        prefs.edit()
            .putString("setting_theme", settings.theme.name)
            .putString("setting_control_mode", settings.controlMode.name)
            .putBoolean("setting_sound_enabled", settings.isSoundEnabled)
            .putBoolean("setting_music_enabled", settings.isMusicEnabled)
            .putFloat("setting_sound_vol", settings.soundVolume)
            .putFloat("setting_music_vol", settings.musicVolume)
            .putBoolean("setting_haptics_enabled", settings.isHapticsEnabled)
            .putBoolean("setting_ghost_enabled", settings.isGhostPieceEnabled)
            .putFloat("setting_button_opacity", settings.buttonOpacity)
            .putFloat("setting_sensitivity", settings.touchSensitivity)
            .apply()
    }

    fun loadStats(): GameStats {
        val stageSetStrings = prefs.getStringSet("stats_completed_stages", emptySet()) ?: emptySet()
        val stageSet = stageSetStrings.mapNotNull { it.toIntOrNull() }.toSet()

        return GameStats(
            classicHighScore = prefs.getLong("stats_classic_high", 0),
            sprintBestTimeMs = prefs.getLong("stats_sprint_best_ms", 0),
            timeAttackHighScore = prefs.getLong("stats_time_attack_high", 0),
            zenHighScore = prefs.getLong("stats_zen_high", 0),
            totalGamesPlayed = prefs.getInt("stats_games_played", 0),
            totalLinesCleared = prefs.getLong("stats_lines_cleared", 0),
            highestLevel = prefs.getInt("stats_highest_level", 1),
            longestCombo = prefs.getInt("stats_longest_combo", 0),
            totalQuads = prefs.getInt("stats_total_quads", 0),
            completedStages = stageSet
        )
    }

    fun updateClassicScore(score: Long) {
        val current = prefs.getLong("stats_classic_high", 0)
        if (score > current) {
            prefs.edit().putLong("stats_classic_high", score).apply()
        }
    }

    fun updateSprintTime(timeMs: Long) {
        val current = prefs.getLong("stats_sprint_best_ms", 0)
        if (current == 0L || timeMs < current) {
            prefs.edit().putLong("stats_sprint_best_ms", timeMs).apply()
        }
    }

    fun updateTimeAttackScore(score: Long) {
        val current = prefs.getLong("stats_time_attack_high", 0)
        if (score > current) {
            prefs.edit().putLong("stats_time_attack_high", score).apply()
        }
    }

    fun updateZenScore(score: Long) {
        val current = prefs.getLong("stats_zen_high", 0)
        if (score > current) {
            prefs.edit().putLong("stats_zen_high", score).apply()
        }
    }

    fun recordGameFinished(
        linesCleared: Int,
        level: Int,
        maxCombo: Int,
        quadCount: Int
    ) {
        val games = prefs.getInt("stats_games_played", 0) + 1
        val lines = prefs.getLong("stats_lines_cleared", 0) + linesCleared
        val highestLvl = maxOf(prefs.getInt("stats_highest_level", 1), level)
        val longestCmb = maxOf(prefs.getInt("stats_longest_combo", 0), maxCombo)
        val quads = prefs.getInt("stats_total_quads", 0) + quadCount

        prefs.edit()
            .putInt("stats_games_played", games)
            .putLong("stats_lines_cleared", lines)
            .putInt("stats_highest_level", highestLvl)
            .putInt("stats_longest_combo", longestCmb)
            .putInt("stats_total_quads", quads)
            .apply()
    }

    fun markStageCompleted(stageNumber: Int) {
        val current = prefs.getStringSet("stats_completed_stages", emptySet()) ?: emptySet()
        val updated = current.toMutableSet()
        updated.add(stageNumber.toString())
        prefs.edit().putStringSet("stats_completed_stages", updated).apply()
    }

    fun resetStats() {
        prefs.edit()
            .remove("stats_classic_high")
            .remove("stats_sprint_best_ms")
            .remove("stats_time_attack_high")
            .remove("stats_zen_high")
            .remove("stats_games_played")
            .remove("stats_lines_cleared")
            .remove("stats_highest_level")
            .remove("stats_longest_combo")
            .remove("stats_total_quads")
            .remove("stats_completed_stages")
            .apply()
    }
}
