package com.ordervschaos

import kotlinx.serialization.Serializable

@Serializable
data class CurrentBattleResponse(
    val battleId: Int,
    val leftSide: String,
    val rightSide: String,
    val leftColour: String,
    val rightColour: String,
    val leftEmoji: String,
    val rightEmoji: String,
    val leftScore: Int,
    val rightScore: Int
)

@Serializable
data class VoteRequest(
    val isLeft: Boolean
)

@Serializable
data class PastBattleResponse(
    val date: String,
    val leftSide: String,
    val rightSide: String,
    val leftPercentage: Double,
    val rightPercentage: Double
)
