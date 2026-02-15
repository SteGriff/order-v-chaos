package com.ordervschaos

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.slf4j.LoggerFactory
import java.io.File

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val logger = LoggerFactory.getLogger("Application")
    
    // Database configuration
    val dbConfig = DatabaseConfig(
        jdbcUrl = System.getenv("DATABASE_URL") ?: environment.config.propertyOrNull("database.jdbcUrl")?.getString() 
            ?: "jdbc:postgresql://localhost:5432/ordervschaos",
        user = System.getenv("DATABASE_USER") ?: environment.config.propertyOrNull("database.user")?.getString() 
            ?: "postgres",
        password = System.getenv("DATABASE_PASSWORD") ?: environment.config.propertyOrNull("database.password")?.getString() 
            ?: "postgres",
        maxPoolSize = environment.config.propertyOrNull("database.maxPoolSize")?.getString()?.toInt() 
            ?: 10
    )
    
    // Initialize database
    DatabaseFactory.init(dbConfig)
    logger.info("Database initialized")
    
    // Start battle scheduler
    val scheduler = BattleScheduler()
    scheduler.ensureCurrentBattle()
    val scope = CoroutineScope(SupervisorJob())
    scheduler.start(scope)
    logger.info("Battle scheduler started")
    
    // Configure plugins
    install(ContentNegotiation) {
        json()
    }
    
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }
    
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logger.error("Unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
        }
    }
    
    // Configure routes
    configureRoutes()
    
    routing {
        staticResources("/", "static") {
            default("index.html")
        }
    }
}
