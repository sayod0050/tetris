package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ControlMode
import com.example.data.GamePreferences
import com.example.data.UserSettings
import com.example.data.VisualTheme
import com.example.game.BagRandomizer
import com.example.game.Board
import com.example.model.GameMode
import com.example.model.Piece
import com.example.model.SRS
import com.example.model.TetrominoType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun testAppNameResource() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Block Drop", appName)
    }

    @Test
    fun testTetrominoShapesAndRotations() {
        for (type in TetrominoType.values()) {
            for (rotation in 0..3) {
                val offsets = type.getOffsets(rotation)
                assertEquals(4, offsets.size)
                // Ensure all 4 cells are distinct
                assertEquals(4, offsets.toSet().size)
            }
        }
    }

    @Test
    fun testBoardCollisionDetection() {
        val board = Board(10, 20)

        // Valid starting position
        val pieceInCenter = Piece(TetrominoType.T, rotation = 0, x = 4, y = 5)
        assertTrue(board.isValidPosition(pieceInCenter))

        // Out of left bound
        val pieceOffLeft = Piece(TetrominoType.T, rotation = 0, x = -2, y = 5)
        assertFalse(board.isValidPosition(pieceOffLeft))

        // Out of right bound
        val pieceOffRight = Piece(TetrominoType.T, rotation = 0, x = 9, y = 5)
        assertFalse(board.isValidPosition(pieceOffRight))

        // Out of floor bound
        val pieceBelowFloor = Piece(TetrominoType.T, rotation = 0, x = 4, y = 20)
        assertFalse(board.isValidPosition(pieceBelowFloor))

        // Colliding with an existing locked block at (x=5, y=5)
        board.grid[5][5] = 0
        assertFalse(board.isValidPosition(pieceInCenter))
    }

    @Test
    fun testGhostPieceProjection() {
        val board = Board(10, 20)
        val piece = Piece(TetrominoType.O, rotation = 0, x = 4, y = 0)
        val ghost = board.getGhostPiece(piece)

        // For O-piece with cells at y, y+1 in an empty board, lowest y is 18
        assertEquals(18, ghost.y)
        assertTrue(board.isValidPosition(ghost))
        // Ghost moved 1 lower should hit the floor
        assertFalse(board.isValidPosition(ghost.moved(0, 1)))
    }

    @Test
    fun testLineClearAndDrop() {
        val board = Board(10, 20)

        // Fill bottom row completely
        for (x in 0 until 10) {
            board.grid[19][x] = 1
        }
        // Place one block on row 18
        board.grid[18][3] = 2

        val cleared = board.clearLines()
        assertEquals(listOf(19), cleared)

        // Row 19 should now have the block that dropped from row 18
        assertEquals(2, board.grid[19][3])
        // Row 18 should now be empty
        assertEquals(null, board.grid[18][3])
    }

    @Test
    fun testSRSWallKicks() {
        val board = Board(10, 20)
        // I piece at left wall (x=0) in vertical orientation (rotation=1)
        val pieceAtWall = Piece(TetrominoType.I, rotation = 1, x = 0, y = 5)
        val rotated = board.tryRotate(pieceAtWall, clockwise = false)
        assertNotNull(rotated)
        assertTrue(board.isValidPosition(rotated!!))
    }

    @Test
    fun test7BagDistribution() {
        val randomizer = BagRandomizer(12345L)
        val seen = mutableSetOf<TetrominoType>()
        for (i in 0 until 7) {
            seen.add(randomizer.nextPiece())
        }
        // All 7 distinct types must be drawn in the first 7 pieces
        assertEquals(7, seen.size)
    }

    @Test
    fun testGamePreferencesPersistence() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = GamePreferences(context)

        prefs.updateClassicScore(12500L)
        prefs.updateSprintTime(65432L)

        val stats = prefs.loadStats()
        assertEquals(12500L, stats.classicHighScore)
        assertEquals(65432L, stats.sprintBestTimeMs)

        val newSettings = UserSettings(
            theme = VisualTheme.MIDNIGHT_OLED,
            controlMode = ControlMode.BUTTONS_ONLY,
            isHapticsEnabled = false
        )
        prefs.saveSettings(newSettings)

        val loadedSettings = prefs.loadSettings()
        assertEquals(VisualTheme.MIDNIGHT_OLED, loadedSettings.theme)
        assertEquals(ControlMode.BUTTONS_ONLY, loadedSettings.controlMode)
        assertFalse(loadedSettings.isHapticsEnabled)
    }
}
