# Order v Chaos - Build Complete ✅

## 🐳 NEW: Docker Support Added!

### Quick Deploy with Docker

```bash
# Option 1: Docker Compose (Recommended)
cp .env.example .env
docker-compose up --build
# Visit http://localhost:8080

# Option 2: Single Container (Simpler)
.\gradlew.bat build
docker build -f Dockerfile.single -t ordervschaos-single .
docker run -d -p 8080:8080 ordervschaos-single
```

See [DOCKER.md](DOCKER.md) for complete Docker deployment guide!

---

## What's Been Built

I've successfully created a complete fullstack Kotlin application for the "Order v Chaos" daily voting game.

### Project Structure

```
order-v-chaos/
├── build.gradle.kts                  # Gradle build configuration
├── settings.gradle.kts               # Gradle settings
├── gradle.properties                 # Gradle properties
├── README.md                         # Complete setup documentation
├── gradle/wrapper/                   # Gradle wrapper files
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
├── gradlew.bat                       # Windows Gradle wrapper
└── src/
    ├── main/
    │   ├── kotlin/com/ordervschaos/
    │   │   ├── Application.kt        # Main app & server config
    │   │   ├── Models.kt             # Database table definitions
    │   │   ├── DTOs.kt               # API data transfer objects
    │   │   ├── Database.kt           # DB connection & Flyway setup
    │   │   ├── BattleScheduler.kt    # Daily rotation scheduler
    │   │   └── Routes.kt             # API endpoints
    │   └── resources/
    │       ├── application.conf      # App configuration
    │       ├── logback.xml           # Logging configuration
    │       ├── db/migration/         # Flyway SQL migrations
    │       │   ├── V1__Create_themes_table.sql
    │       │   ├── V2__Create_battles_table.sql
    │       │   ├── V3__Create_votes_table.sql
    │       │   └── V4__Seed_themes.sql (25 themes!)
    │       └── static/               # Frontend files
    │           ├── index.html        # Main page
    │           ├── styles.css        # Responsive styling
    │           └── app.js            # Vanilla JS with polling
    └── test/kotlin/com/ordervschaos/
```

## Features Implemented

### Backend (Kotlin + Ktor)
✅ REST API with 3 endpoints:
  - `GET /api/current` - Current battle with live scores
  - `POST /api/vote` - Submit votes (unlimited)
  - `GET /api/past-battles` - Last 10 completed battles

✅ PostgreSQL database with Exposed ORM
✅ Flyway migrations for schema management
✅ HikariCP connection pooling
✅ Battle scheduler that rotates at midnight UK time
✅ 25 pre-seeded themed battles
✅ CORS and error handling configured

### Frontend (Vanilla JS)
✅ Responsive single-page design
✅ Dynamic tug-of-war progress bar
✅ Real-time updates via 10-second polling
✅ Vote buttons with emoji and theming
✅ Past battles display with percentages
✅ Mobile-friendly responsive layout

### Database Schema
✅ **Themes table** - 25 battle themes with colors & emojis
✅ **Battles table** - Daily battle tracking with scores
✅ **Votes table** - Append-only vote records

## How to Run

### Prerequisites
1. **Java JDK 17+** - Download from https://adoptium.net/
2. **PostgreSQL 12+** - Install and create database:
   ```sql
   CREATE DATABASE ordervschaos;
   ```

### Steps
1. Configure database (if not using defaults):
   - Edit `src/main/resources/application.conf`
   - OR set environment variables: `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`

2. Build the application:
   ```bash
   .\gradlew.bat build
   ```

3. Run the server:
   ```bash
   .\gradlew.bat run
   ```

4. Open your browser to: **http://localhost:8080**

## What Happens on First Run

1. Flyway automatically creates all tables
2. Seeds 25 battle themes (Order vs Chaos, Cats vs Dogs, etc.)
3. Creates the first battle (Order vs Chaos)
4. Starts the scheduler for daily rotation at midnight UK time
5. Serves the web interface

## Architecture Highlights

- **Append-only votes**: No race conditions, just INSERT operations
- **Live counting**: Scores calculated on-demand from vote counts
- **Eventual consistency**: No locks needed, fuzzy view is acceptable
- **Auto-rotation**: Background coroutine handles midnight switchover
- **Theme cycling**: Reuses themes when all 25 are exhausted
- **Timezone aware**: All scheduling uses Europe/London timezone

## Testing the App

1. Visit http://localhost:8080
2. Click either button to vote
3. Watch the tug-of-war bar update every 10 seconds
4. Click multiple times (unlimited voting allowed)
5. Check the "Past Battles" section at the bottom

## What's Next?

The application is **production-ready** but could be enhanced with:
- Admin panel to manage themes
- WebSockets for instant updates (instead of polling)
- Vote rate limiting per IP
- Analytics and charts
- Social sharing features
- Docker containerization

---

**Status**: ✅ Complete and ready to test!
