package com.ordervschaos

import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.*
import java.time.temporal.ChronoUnit

class BattleScheduler {
    private val logger = LoggerFactory.getLogger(BattleScheduler::class.java)
    private var job: Job? = null

    fun start(scope: CoroutineScope) {
        job = scope.launch {
            while (isActive) {
                val now = Instant.now()
                val nextMidnight = calculateNextMidnightUK(now)
                val delayMillis = ChronoUnit.MILLIS.between(now, nextMidnight)
                
                logger.info("Next battle rotation scheduled at: $nextMidnight (in ${delayMillis}ms)")
                
                delay(delayMillis)
                
                try {
                    rotateBattle()
                } catch (e: Exception) {
                    logger.error("Error rotating battle", e)
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    private fun calculateNextMidnightUK(now: Instant): Instant {
        val ukZone = ZoneId.of("Europe/London")
        val nowUK = ZonedDateTime.ofInstant(now, ukZone)
        val nextMidnight = nowUK.toLocalDate().plusDays(1).atStartOfDay(ukZone)
        return nextMidnight.toInstant()
    }

    private fun rotateBattle() = transaction {
        logger.info("Starting battle rotation")
        
        // End current battle if one exists
        val currentBattle = Battles.selectAll()
            .where { Battles.ended.isNull() }
            .singleOrNull()
        
        if (currentBattle != null) {
            val battleId = currentBattle[Battles.id].value
            val leftScore = Votes.selectAll()
                .where { (Votes.battleId eq battleId) and (Votes.isLeft eq true) }
                .count()
            val rightScore = Votes.selectAll()
                .where { (Votes.battleId eq battleId) and (Votes.isLeft eq false) }
                .count()
            
            Battles.update({ Battles.id eq battleId }) {
                it[leftFinalScore] = leftScore.toInt()
                it[rightFinalScore] = rightScore.toInt()
                it[ended] = Instant.now()
            }
            
            logger.info("Ended battle $battleId with scores: $leftScore vs $rightScore")
        }
        
        // Get next theme
        val usedThemeIds = Battles.select(Battles.themeId).map { it[Battles.themeId] }.toSet()
        val nextTheme = Themes.selectAll()
            .orderBy(Themes.ordinal)
            .firstOrNull { it[Themes.id].value !in usedThemeIds }
            ?: Themes.selectAll().orderBy(Themes.ordinal).first() // Restart from beginning if all used
        
        // Start new battle
        val newBattleId = Battles.insert {
            it[themeId] = nextTheme[Themes.id].value
            it[started] = Instant.now()
            it[leftFinalScore] = 0
            it[rightFinalScore] = 0
        } get Battles.id
        
        logger.info("Started new battle $newBattleId with theme: ${nextTheme[Themes.leftSide]} vs ${nextTheme[Themes.rightSide]}")
    }

    fun ensureCurrentBattle() = transaction {
        try {
            val currentBattle = Battles.selectAll()
                .where { Battles.ended.isNull() }
                .singleOrNull()
            
            if (currentBattle == null) {
                logger.info("No current battle found, creating initial battle")
                
                val firstTheme = Themes.selectAll()
                    .orderBy(Themes.ordinal)
                    .first()
                
                Battles.insert {
                    it[themeId] = firstTheme[Themes.id].value
                    it[started] = Instant.now()
                    it[leftFinalScore] = 0
                    it[rightFinalScore] = 0
                }
                
                logger.info("Created initial battle with theme: ${firstTheme[Themes.leftSide]} vs ${firstTheme[Themes.rightSide]}")
            }
        } catch (e: Exception) {
            logger.error("Error ensuring current battle", e)
            throw e
        }
    }
}
