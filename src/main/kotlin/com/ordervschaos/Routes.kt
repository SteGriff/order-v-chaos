package com.ordervschaos

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Application.configureRoutes() {
    routing {
        get("/api/current") {
            val response = transaction {
                val currentBattle = (Battles innerJoin Themes)
                    .selectAll()
                    .where { Battles.ended.isNull() }
                    .single()
                
                val battleId = currentBattle[Battles.id].value
                
                val leftScore = Votes.selectAll()
                    .where { (Votes.battleId eq battleId) and (Votes.isLeft eq true) }
                    .count()
                
                val rightScore = Votes.selectAll()
                    .where { (Votes.battleId eq battleId) and (Votes.isLeft eq false) }
                    .count()
                
                CurrentBattleResponse(
                    battleId = battleId,
                    leftSide = currentBattle[Themes.leftSide],
                    rightSide = currentBattle[Themes.rightSide],
                    leftColour = currentBattle[Themes.leftColour],
                    rightColour = currentBattle[Themes.rightColour],
                    leftEmoji = currentBattle[Themes.leftEmoji],
                    rightEmoji = currentBattle[Themes.rightEmoji],
                    leftScore = leftScore.toInt(),
                    rightScore = rightScore.toInt()
                )
            }
            
            call.respond(response)
        }
        
        post("/api/vote") {
            val voteRequest = call.receive<VoteRequest>()
            
            transaction {
                val currentBattleId = Battles.selectAll()
                    .where { Battles.ended.isNull() }
                    .single()[Battles.id].value
                
                Votes.insert {
                    it[battleId] = currentBattleId
                    it[isLeft] = voteRequest.isLeft
                    it[created] = Instant.now()
                }
            }
            
            call.respond(HttpStatusCode.Created, mapOf("status" to "ok"))
        }
        
        get("/api/past-battles") {
            val battles = transaction {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    .withZone(ZoneId.of("Europe/London"))
                
                (Battles innerJoin Themes)
                    .selectAll()
                    .where { Battles.ended.isNotNull() }
                    .orderBy(Battles.ended to SortOrder.DESC)
                    .limit(10)
                    .map { row ->
                        val leftScore = row[Battles.leftFinalScore]
                        val rightScore = row[Battles.rightFinalScore]
                        val total = (leftScore + rightScore).toDouble()
                        
                        val leftPercentage = if (total > 0) (leftScore / total) * 100 else 0.0
                        val rightPercentage = if (total > 0) (rightScore / total) * 100 else 0.0
                        
                        PastBattleResponse(
                            date = formatter.format(row[Battles.ended]),
                            leftSide = row[Themes.leftSide],
                            rightSide = row[Themes.rightSide],
                            leftPercentage = leftPercentage,
                            rightPercentage = rightPercentage
                        )
                    }
            }
            
            call.respond(battles)
        }
    }
}
