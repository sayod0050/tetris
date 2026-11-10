package com.example.game

import com.example.model.Piece
import com.example.model.SRS
import com.example.model.TetrominoType

/**
 * 10x20 standard falling-block game grid with collision detection,
 * SRS wall-kick rotations, line clearing, and ghost piece projections.
 */
class Board(
    val width: Int = 10,
    val height: Int = 20
) {
    // grid[y][x] holds the colorIndex (0..6) of the locked block, or null if empty
    val grid: Array<Array<Int?>> = Array(height) { arrayOfNulls<Int>(width) }

    fun clear() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                grid[y][x] = null
            }
        }
    }

    /**
     * Pre-loads garbage rows with random holes for challenge stages.
     */
    fun setupGarbageRows(rowCount: Int) {
        val random = java.util.Random()
        for (y in (height - rowCount) until height) {
            val holeX = random.nextInt(width)
            for (x in 0 until width) {
                if (x != holeX) {
                    grid[y][x] = 0 // Grey/default garbage block
                }
            }
        }
    }

    fun isValidPosition(piece: Piece): Boolean {
        for (cell in piece.cells) {
            val x = cell.first
            val y = cell.second
            // Out of horizontal bounds
            if (x < 0 || x >= width) return false
            // Out of bottom bounds
            if (y >= height) return false
            // Colliding with locked block
            if (y >= 0 && grid[y][x] != null) return false
        }
        return true
    }

    /**
     * Attempts rotation with SRS (Super Rotation System) wall kicks.
     * Returns the rotated piece with kick applied, or null if rotation is blocked.
     */
    fun tryRotate(current: Piece, clockwise: Boolean): Piece? {
        val targetRot = if (clockwise) (current.rotation + 1) % 4 else (current.rotation + 3) % 4
        val kickOffsets = SRS.getKicks(current.type, current.rotation, targetRot)

        for (kick in kickOffsets) {
            // Note: SRS kick table convention (dx, dy where +dy is up in math, so in screen coords -dy is up)
            val testPiece = current.copy(
                rotation = targetRot,
                x = current.x + kick.first,
                y = current.y - kick.second
            )
            if (isValidPosition(testPiece)) {
                return testPiece
            }
        }
        return null
    }

    /**
     * Calculates the ghost piece projection at the lowest valid landing position.
     */
    fun getGhostPiece(piece: Piece): Piece {
        var ghost = piece
        while (isValidPosition(ghost.moved(0, 1))) {
            ghost = ghost.moved(0, 1)
        }
        return ghost
    }

    /**
     * Locks the piece into the board grid.
     * Returns true if any locked cell landed above the visible board (Game Over).
     */
    fun lockPiece(piece: Piece): Boolean {
        var toppedOut = true
        for (cell in piece.cells) {
            val x = cell.first
            val y = cell.second
            if (y >= 0 && y < height && x >= 0 && x < width) {
                grid[y][x] = piece.type.colorIndex
                toppedOut = false
            }
        }
        return toppedOut
    }

    /**
     * Checks for completed lines, removes them, and returns the cleared row indices.
     */
    fun clearLines(): List<Int> {
        val clearedRows = mutableListOf<Int>()
        for (y in 0 until height) {
            var full = true
            for (x in 0 until width) {
                if (grid[y][x] == null) {
                    full = false
                    break
                }
            }
            if (full) {
                clearedRows.add(y)
            }
        }

        if (clearedRows.isNotEmpty()) {
            // Drop rows down
            for (clearedRow in clearedRows) {
                for (y in clearedRow downTo 1) {
                    for (x in 0 until width) {
                        grid[y][x] = grid[y - 1][x]
                    }
                }
                for (x in 0 until width) {
                    grid[0][x] = null
                }
            }
        }

        return clearedRows
    }
}
