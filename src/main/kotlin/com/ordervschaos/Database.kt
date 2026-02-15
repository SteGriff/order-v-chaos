package com.ordervschaos

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    private val logger = org.slf4j.LoggerFactory.getLogger(DatabaseFactory::class.java)
    
    fun init(config: DatabaseConfig) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            username = config.user
            password = config.password
            maximumPoolSize = config.maxPoolSize
            driverClassName = "org.postgresql.Driver"
        }
        
        val dataSource = HikariDataSource(hikariConfig)
        
        // Run migrations
        logger.info("Running Flyway migrations...")
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .load()
        val result = flyway.migrate()
        logger.info("Flyway migrations complete. Applied ${result.migrationsExecuted} migrations")
        
        // Connect Exposed
        Database.connect(dataSource)
        logger.info("Database connection established")
    }
}

data class DatabaseConfig(
    val jdbcUrl: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int
)
