package com.example.model

/**
 * The 7 standard tetromino shapes with distinctive theme color indices
 * and exact coordinate layouts for all 4 rotation states.
 */
enum class TetrominoType(
    val colorIndex: Int,
    val spawnX: Int,
    val spawnY: Int
) {
    I(0, 3, 0),
    O(1, 4, 0),
    T(2, 3, 0),
    S(3, 3, 0),
    Z(4, 3, 0),
    J(5, 3, 0),
    L(6, 3, 0);

    /**
     * Relative coordinates (x, y) for each of the 4 cells in the 4 rotation states (0, 1, 2, 3).
     */
    fun getOffsets(rotation: Int): List<Pair<Int, Int>> {
        val r = (rotation % 4 + 4) % 4
        return when (this) {
            I -> when (r) {
                0 -> listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(3, 1))
                1 -> listOf(Pair(2, 0), Pair(2, 1), Pair(2, 2), Pair(2, 3))
                2 -> listOf(Pair(0, 2), Pair(1, 2), Pair(2, 2), Pair(3, 2))
                else -> listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(1, 3))
            }
            O -> listOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1))
            T -> when (r) {
                0 -> listOf(Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1))
                1 -> listOf(Pair(1, 0), Pair(1, 1), Pair(2, 1), Pair(1, 2))
                2 -> listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(1, 2))
                else -> listOf(Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(1, 2))
            }
            S -> when (r) {
                0 -> listOf(Pair(1, 0), Pair(2, 0), Pair(0, 1), Pair(1, 1))
                1 -> listOf(Pair(1, 0), Pair(1, 1), Pair(2, 1), Pair(2, 2))
                2 -> listOf(Pair(1, 1), Pair(2, 1), Pair(0, 2), Pair(1, 2))
                else -> listOf(Pair(0, 0), Pair(0, 1), Pair(1, 1), Pair(1, 2))
            }
            Z -> when (r) {
                0 -> listOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(2, 1))
                1 -> listOf(Pair(2, 0), Pair(1, 1), Pair(2, 1), Pair(1, 2))
                2 -> listOf(Pair(0, 1), Pair(1, 1), Pair(1, 2), Pair(2, 2))
                else -> listOf(Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(0, 2))
            }
            J -> when (r) {
                0 -> listOf(Pair(0, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1))
                1 -> listOf(Pair(1, 0), Pair(2, 0), Pair(1, 1), Pair(1, 2))
                2 -> listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(2, 2))
                else -> listOf(Pair(1, 0), Pair(1, 1), Pair(0, 2), Pair(1, 2))
            }
            L -> when (r) {
                0 -> listOf(Pair(2, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1))
                1 -> listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(2, 2))
                2 -> listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(0, 2))
                else -> listOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(1, 2))
            }
        }
    }
}

data class Piece(
    val type: TetrominoType,
    val rotation: Int = 0,
    val x: Int = type.spawnX,
    val y: Int = type.spawnY
) {
    /**
     * Absolute board coordinates for the 4 cells of this piece.
     */
    val cells: List<Pair<Int, Int>>
        get() = type.getOffsets(rotation).map { Pair(x + it.first, y + it.second) }

    fun moved(dx: Int, dy: Int): Piece = copy(x = x + dx, y = y + dy)

    fun rotated(clockwise: Boolean): Piece {
        val newRot = if (clockwise) (rotation + 1) % 4 else (rotation + 3) % 4
        return copy(rotation = newRot)
    }
}
