package com.ordervschaos

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Themes : IntIdTable("themes") {
    val leftSide = varchar("left_side", 100)
    val rightSide = varchar("right_side", 100)
    val leftColour = varchar("left_colour", 7)
    val rightColour = varchar("right_colour", 7)
    val leftEmoji = varchar("left_emoji", 10)
    val rightEmoji = varchar("right_emoji", 10)
    val ordinal = integer("ordinal")
}

object Battles : IntIdTable("battles") {
    val themeId = integer("theme_id").references(Themes.id)
    val leftFinalScore = integer("left_final_score").default(0)
    val rightFinalScore = integer("right_final_score").default(0)
    val started = timestamp("started")
    val ended = timestamp("ended").nullable()
}

object Votes : IntIdTable("votes") {
    val battleId = integer("battle_id").references(Battles.id)
    val isLeft = bool("is_left")
    val created = timestamp("created")
}
