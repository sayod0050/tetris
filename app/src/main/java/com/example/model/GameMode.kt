package com.example.model

enum class GameMode(
    val title: String,
    val subtitle: String,
    val description: String
) {
    CLASSIC(
        "Classic",
        "Marathon",
        "Standard rules. Levels increase every 10 lines. Play until top out."
    ),
    SPRINT(
        "Sprint (40L)",
        "Speed Run",
        "Clear 40 lines as fast as you can. Beat your personal record."
    ),
    TIME_ATTACK(
        "Time Attack",
        "2-Minute Rush",
        "Score as many points as possible before the 120-second timer expires."
    ),
    ZEN(
        "Zen Mode",
        "Endless Calm",
        "Relaxing puzzle with no rush. Perfect for winding down."
    ),
    CHALLENGE(
        "Challenge",
        "10 Objective Stages",
        "Progressive stages with custom objectives and pre-set board puzzles."
    )
}

data class ChallengeObjective(
    val stageNumber: Int,
    val title: String,
    val goalDescription: String,
    val targetLines: Int = 0,
    val targetScore: Int = 0,
    val targetQuads: Int = 0,
    val targetCombo: Int = 0,
    val timeLimitSec: Int = 0,
    val startingGarbageLines: Int = 0,
    val startLevel: Int = 1
)

object ChallengeStages {
    val STAGES = listOf(
        ChallengeObjective(
            stageNumber = 1,
            title = "First Steps",
            goalDescription = "Clear 10 lines to prove your basics.",
            targetLines = 10
        ),
        ChallengeObjective(
            stageNumber = 2,
            title = "Quick Thinking",
            goalDescription = "Score 2,500 points in 60 seconds.",
            targetScore = 2500,
            timeLimitSec = 60
        ),
        ChallengeObjective(
            stageNumber = 3,
            title = "Quad Hunter",
            goalDescription = "Execute 2 Quads (4-line drops).",
            targetQuads = 2
        ),
        ChallengeObjective(
            stageNumber = 4,
            title = "Combo Striker",
            goalDescription = "Reach a 4x or higher combo chain.",
            targetCombo = 4
        ),
        ChallengeObjective(
            stageNumber = 5,
            title = "Garbage Cleanup",
            goalDescription = "Clear 15 lines starting with 5 messy rows.",
            targetLines = 15,
            startingGarbageLines = 5
        ),
        ChallengeObjective(
            stageNumber = 6,
            title = "Sprint Trial",
            goalDescription = "Clear 20 lines under 45 seconds.",
            targetLines = 20,
            timeLimitSec = 45
        ),
        ChallengeObjective(
            stageNumber = 7,
            title = "Double Quad",
            goalDescription = "Perform 3 Quads to demonstrate mastery.",
            targetQuads = 3
        ),
        ChallengeObjective(
            stageNumber = 8,
            title = "High Velocity",
            goalDescription = "Clear 15 lines at Level 7 gravity.",
            targetLines = 15,
            startLevel = 7
        ),
        ChallengeObjective(
            stageNumber = 9,
            title = "Deep Rubble",
            goalDescription = "Clear 20 lines starting with 8 garbage rows.",
            targetLines = 20,
            startingGarbageLines = 8
        ),
        ChallengeObjective(
            stageNumber = 10,
            title = "Grandmaster",
            goalDescription = "Score 15,000 points starting at Level 10.",
            targetScore = 15000,
            startLevel = 10
        )
    )
}
