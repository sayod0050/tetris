package com.example.game

import com.example.model.TetrominoType

/**
 * Fair 7-Bag Randomizer generator as standardized in modern falling-block puzzle games.
 * Guarantees uniform distribution and eliminates dry streaks.
 */
class BagRandomizer(private val randomSeed: Long? = null) {

    private val random = if (randomSeed != null) java.util.Random(randomSeed) else java.util.Random()
    private val bag = ArrayDeque<TetrominoType>()

    private fun refillBag() {
        val pieces = TetrominoType.values().toMutableList()
        // Fisher-Yates shuffle
        for (i in pieces.size - 1 downTo 1) {
            val j = random.nextInt(i + 1)
            val temp = pieces[i]
            pieces[i] = pieces[j]
            pieces[j] = temp
        }
        bag.addAll(pieces)
    }

    fun nextPiece(): TetrominoType {
        if (bag.isEmpty()) {
            refillBag()
        }
        return bag.removeFirst()
    }

    /**
     * Peek the next [count] pieces without consuming them.
     */
    fun peekQueue(count: Int): List<TetrominoType> {
        while (bag.size < count) {
            refillBag()
        }
        return bag.take(count)
    }

    fun reset() {
        bag.clear()
        refillBag()
    }
}
