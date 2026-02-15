# Order v Chaos - Daily Battle Game

A Kotlin fullstack web application where users vote in daily themed battles. Every day at midnight (UK time), a new battle begins between two opposing concepts (e.g., Order vs Chaos, Cats vs Dogs, etc.).

## Tech Stack

- **Backend**: Kotlin, Ktor
- **Database**: PostgreSQL with Exposed ORM
- **Migrations**: Flyway
- **Frontend**: Vanilla JavaScript with 10-second polling

## Features

- Anonymous voting (no login required)
- Real-time tug-of-war visualization
- Automatic daily battle rotation at midnight UK time
- 25 pre-seeded battle themes that cycle
- Historical view of past battles with percentages

## Prerequisites

- JDK 21 or higher (JetBrains Runtime from IntelliJ IDEA works great)
- PostgreSQL 12 or higher
- Gradle 8.5 (wrapper included)
- PowerShell 5.1+ (Windows) or PowerShell Core 6+ (cross-platform)

## Setup

### 1. Database Setup

Create a PostgreSQL database:

**PowerShell:**
```powershell
$env:PGPASSWORD = "your_postgres_password"
& "C:\Program Files\PostgreSQL\18\bin\createdb.exe" -U postgres ordervschaos
```

Or using psql:
```powershell
psql -U postgres
```
```sql
CREATE DATABASE ordervschaos;
\q
```

### 2. Configuration

The application reads configuration from `src/main/resources/application.conf` with environment variable overrides.

**Set your database password:**
```powershell
$env:DATABASE_PASSWORD = "your_postgres_password"
```

**Optional - configure Java (if using JetBrains Toolbox JDK):**
```powershell
$env:JAVA_HOME = "C:\Users\YOUR_USERNAME\AppData\Local\Programs\IntelliJ IDEA\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

You can also override other settings:
```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/ordervschaos"
$env:DATABASE_USER = "postgres"
```

### 3. Quick Setup Script

Run the PowerShell setup helper:

```powershell
.\setup.ps1
```

This will check your Java and PostgreSQL installation and guide you through setup.

### 4. Build and Run

**Build the project:**
```powershell
.\gradlew.bat build
```

**Run the application:**
```powershell
$env:DATABASE_PASSWORD = "your_postgres_password"
.\gradlew.bat run
```

**Or combine in one line:**
```powershell
$env:DATABASE_PASSWORD = "your_postgres_password"; .\gradlew.bat run
```

The application will:
1. Run Flyway migrations to create tables
2. Seed 25 battle themes
3. Create the first battle if none exists
4. Start the scheduler for daily rotations
5. Serve the web app on http://localhost:8080

**On first run**, you'll see:
```
Flyway migrations complete. Applied X migrations
Database connection established
Database initialized
Created initial battle with theme: Order vs Chaos
Battle scheduler started
Next battle rotation scheduled at: 2026-02-02T00:00:00Z
Application started in X.XXX seconds.
Responding at http://127.0.0.1:8080
```

## API Endpoints

- `GET /api/current` - Get current battle with live scores
- `POST /api/vote` - Submit a vote `{isLeft: boolean}`
- `GET /api/past-battles` - Get last 10 completed battles with percentages

## Database Schema

### Themes
- 25 pre-seeded battle themes with colors and emojis
- Cycles through themes, restarting when all are used

### Battles
- Tracks each daily battle
- `ended` is NULL for the current active battle

### Votes
- Append-only table of all votes
- No user tracking, completely anonymous

## How It Works

1. Users visit the site and see the current battle
2. They can click either button to vote (unlimited clicks)
3. Frontend polls `/api/current` every 10 seconds for live updates
4. At midnight UK time, the scheduler:
   - Ends the current battle
   - Counts final votes and stores totals
   - Starts the next battle with a new theme
5. Past battles display with calculated percentages

## Development

The project structure:

```
src/main/kotlin/com/ordervschaos/
├── Application.kt        # Main entry point and server config
├── Models.kt            # Exposed table definitions
├── DTOs.kt              # API request/response models
├── Database.kt          # Database connection setup
├── BattleScheduler.kt   # Daily rotation logic
└── Routes.kt            # API endpoint handlers

src/main/resources/
├── application.conf     # Configuration
├── logback.xml         # Logging config
├── db/migration/       # Flyway SQL migrations
└── static/             # Frontend files
    ├── index.html
    ├── styles.css
    └── app.js
```

## Testing

Run tests with:

```powershell
.\gradlew.bat test
```

## Common Issues

### "JAVA_HOME is not set"
Set JAVA_HOME to your JDK installation:
```powershell
$env:JAVA_HOME = "C:\Users\YOUR_USERNAME\AppData\Local\Programs\IntelliJ IDEA\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

### "password authentication failed"
Make sure DATABASE_PASSWORD environment variable is set:
```powershell
$env:DATABASE_PASSWORD = "your_actual_postgres_password"
```

### "relation does not exist"
The database migrations haven't run. This usually means the database connection failed. Check:
1. PostgreSQL is running
2. Database `ordervschaos` exists
3. DATABASE_PASSWORD is correct

### Port 8080 already in use
Change the port in `src/main/resources/application.conf`:
```
ktor {
    deployment {
        port = 8081
    }
}
```

## Docker Deployment

See [DOCKER.md](DOCKER.md) for complete Docker deployment instructions.

### Quick Start with Docker

**PowerShell:**
```powershell
# Copy environment file and set password
Copy-Item .env.example .env
# Edit .env and set DATABASE_PASSWORD=your_password

# Build and run with Docker Compose
docker-compose up --build

# Access at http://localhost:8080
```

This runs both PostgreSQL and the application in separate containers (recommended approach).

### Quick Start without Docker

**PowerShell:**
```powershell
# Set environment variables
$env:DATABASE_PASSWORD = "your_postgres_password"

# Run the application
.\gradlew.bat run

# Access at http://localhost:8080
```

## Stopping the Application

**Local run:**
- Press `Ctrl+C` in the terminal

**Docker:**
```powershell
docker-compose down
```

## Development

The project structure:
