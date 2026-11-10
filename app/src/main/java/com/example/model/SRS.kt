package com.example.model

/**
 * Super Rotation System (SRS) Wall Kick Offset tables.
 * Enables smooth rotation against walls and existing blocks.
 */
object SRS {

    // Standard JLSTZ wall kick offsets for rotation transitions
    private val JLSTZ_KICKS = mapOf(
        Pair(0, 1) to listOf(Pair(0, 0), Pair(-1, 0), Pair(-1, -1), Pair(0, 2), Pair(-1, 2)),
        Pair(1, 0) to listOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(0, -2), Pair(1, -2)),
        Pair(1, 2) to listOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(0, -2), Pair(1, -2)),
        Pair(2, 1) to listOf(Pair(0, 0), Pair(-1, 0), Pair(-1, -1), Pair(0, 2), Pair(-1, 2)),
        Pair(2, 3) to listOf(Pair(0, 0), Pair(1, 0), Pair(1, -1), Pair(0, 2), Pair(1, 2)),
        Pair(3, 2) to listOf(Pair(0, 0), Pair(-1, 0), Pair(-1, 1), Pair(0, -2), Pair(-1, -2)),
        Pair(3, 0) to listOf(Pair(0, 0), Pair(-1, 0), Pair(-1, 1), Pair(0, -2), Pair(-1, -2)),
        Pair(0, 3) to listOf(Pair(0, 0), Pair(1, 0), Pair(1, -1), Pair(0, 2), Pair(1, 2))
    )

    // Standard I piece wall kick offsets
    private val I_KICKS = mapOf(
        Pair(0, 1) to listOf(Pair(0, 0), Pair(-2, 0), Pair(1, 0), Pair(-2, 1), Pair(1, -2)),
        Pair(1, 0) to listOf(Pair(0, 0), Pair(2, 0), Pair(-1, 0), Pair(2, -1), Pair(-1, 2)),
        Pair(1, 2) to listOf(Pair(0, 0), Pair(-1, 0), Pair(2, 0), Pair(-1, -2), Pair(2, 1)),
        Pair(2, 1) to listOf(Pair(0, 0), Pair(1, 0), Pair(-2, 0), Pair(1, 2), Pair(-2, -1)),
        Pair(2, 3) to listOf(Pair(0, 0), Pair(2, 0), Pair(-1, 0), Pair(2, -1), Pair(-1, 2)),
        Pair(3, 2) to listOf(Pair(0, 0), Pair(-2, 0), Pair(1, 0), Pair(-2, 1), Pair(1, -2)),
        Pair(3, 0) to listOf(Pair(0, 0), Pair(1, 0), Pair(-2, 0), Pair(1, 2), Pair(-2, -1)),
        Pair(0, 3) to listOf(Pair(0, 0), Pair(-1, 0), Pair(2, 0), Pair(-1, -2), Pair(2, 1))
    )

    fun getKicks(type: TetrominoType, fromRot: Int, toRot: Int): List<Pair<Int, Int>> {
        if (type == TetrominoType.O) {
            return listOf(Pair(0, 0))
        }
        val key = Pair(fromRot, toRot)
        return if (type == TetrominoType.I) {
            I_KICKS[key] ?: listOf(Pair(0, 0))
        } else {
            JLSTZ_KICKS[key] ?: listOf(Pair(0, 0))
        }
    }
}
