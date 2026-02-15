# Order v Chaos - Quick Reference (PowerShell)

## First Time Setup

```powershell
# 1. Run setup script
.\setup.ps1

# 2. Create database
$env:PGPASSWORD = "your_postgres_password"
& "C:\Program Files\PostgreSQL\18\bin\createdb.exe" -U postgres ordervschaos

# 3. Set password environment variable
$env:DATABASE_PASSWORD = "your_postgres_password"

# 4. Build and run
.\gradlew.bat build
.\gradlew.bat run

# 5. Open browser
# Visit: http://localhost:8080
```

## Daily Development

```powershell
# Start the app
$env:DATABASE_PASSWORD = "your_password"; .\gradlew.bat run

# Or separate commands
$env:DATABASE_PASSWORD = "your_password"
.\gradlew.bat run
```

## Docker

```powershell
# First time
Copy-Item .env.example .env
# Edit .env to set DATABASE_PASSWORD

# Run
docker-compose up --build

# Stop
docker-compose down

# Clean restart (deletes data!)
docker-compose down -v
docker-compose up --build
```

## Common Commands

```powershell
# Build only
.\gradlew.bat build

# Clean build
.\gradlew.bat clean build

# Run tests
.\gradlew.bat test

# Check Java version
java -version

# Check PostgreSQL connection
$env:PGPASSWORD = "password"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d ordervschaos -c "SELECT 1;"
```

## Troubleshooting

```powershell
# Fix JAVA_HOME
$env:JAVA_HOME = "C:\Users\$env:USERNAME\AppData\Local\Programs\IntelliJ IDEA\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Check if app is running
Invoke-WebRequest http://localhost:8080/api/current

# View Docker logs
docker-compose logs -f app

# Restart Docker containers
docker-compose restart app
```

## Environment Variables

```powershell
# Required
$env:DATABASE_PASSWORD = "your_password"

# Optional (with defaults)
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/ordervschaos"
$env:DATABASE_USER = "postgres"
$env:JAVA_HOME = "path\to\jdk"
```

## Useful PowerShell Tips

```powershell
# Set multiple variables at once
$env:DATABASE_PASSWORD = "password"; $env:DATABASE_USER = "postgres"

# Persist environment variables for session
$env:DATABASE_PASSWORD = "password"
# Now DATABASE_PASSWORD is available for all commands in this session

# Check environment variable
echo $env:DATABASE_PASSWORD

# Clear environment variable
Remove-Item Env:\DATABASE_PASSWORD
```

## File Locations

- **Config**: `src\main\resources\application.conf`
- **Frontend**: `src\main\resources\static\`
- **Migrations**: `src\main\resources\db\migration\`
- **Logs**: Console output
- **Docker logs**: `docker-build.log`, `docker-run.log`

## URLs

- **Application**: http://localhost:8080
- **Current Battle API**: http://localhost:8080/api/current
- **Vote API**: http://localhost:8080/api/vote (POST)
- **Past Battles API**: http://localhost:8080/api/past-battles

## Quick Test

```powershell
# Test API
Invoke-WebRequest http://localhost:8080/api/current | Select-Object -ExpandProperty Content

# Submit a vote
$body = '{"isLeft":true}' | ConvertTo-Json
Invoke-WebRequest -Uri http://localhost:8080/api/vote -Method POST -Body $body -ContentType "application/json"
```
