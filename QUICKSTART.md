# Order v Chaos - Quick Reference (PowerShell)

## First Time Setup

```powershell
# 1. Run setup script
.\setup.ps1

# 2. Start PostgreSQL with Docker (recommended)
docker run -d `
  --name ordervschaos-postgres `
  -e POSTGRES_PASSWORD=postgres `
  -e POSTGRES_DB=ordervschaos `
  -p 5432:5432 `
  -v ordervschaos-data:/var/lib/postgresql/data `
  postgres:18

# OR if using installed PostgreSQL
$env:PGPASSWORD = "your_postgres_password"
& "C:\Program Files\PostgreSQL\18\bin\createdb.exe" -U postgres ordervschaos

# 3. Set password and run
$env:DATABASE_PASSWORD = "postgres"
.\gradlew.bat run

# 4. Open browser
# Visit: http://localhost:8080
```

## Daily Development

```powershell
# Start PostgreSQL (if using Docker)
docker start ordervschaos-postgres

# Start the app
$env:DATABASE_PASSWORD = "postgres"
.\gradlew.bat run
```

## Docker (Optional - matches production)

```powershell
# Start PostgreSQL
docker run -d `
  --name ordervschaos-postgres `
  -e POSTGRES_PASSWORD=postgres `
  -e POSTGRES_DB=ordervschaos `
  -p 5432:5432 `
  -v ordervschaos-data:/var/lib/postgresql/data `
  postgres:18

# Build and run app
docker build -t ordervschaos .
docker run -d `
  --name ordervschaos `
  -p 8080:8080 `
  -e DATABASE_URL="jdbc:postgresql://ordervschaos-postgres:5432/ordervschaos" `
  -e DATABASE_PASSWORD="postgres" `
  --link ordervschaos-postgres:ordervschaos-postgres `
  ordervschaos

# Stop everything
docker stop ordervschaos ordervschaos-postgres
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

# Check PostgreSQL (Docker)
docker ps | findstr postgres

# Check PostgreSQL (installed)
$env:PGPASSWORD = "password"
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d ordervschaos -c "SELECT 1;"
```

## Environment Variables

```powershell
# Required
$env:DATABASE_PASSWORD = "postgres"

# Optional (with defaults)
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/ordervschaos"
$env:DATABASE_USER = "postgres"
$env:PORT = "8080"
$env:JAVA_HOME = "path\to\jdk"
```

## Troubleshooting

```powershell
# Fix JAVA_HOME
$env:JAVA_HOME = "C:\Users\$env:USERNAME\AppData\Local\Programs\IntelliJ IDEA\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Check if app is running
Invoke-WebRequest http://localhost:8080/api/current

# View Docker logs (if using Docker)
docker logs -f ordervschaos
docker logs -f ordervschaos-postgres

# Restart PostgreSQL (Docker)
docker restart ordervschaos-postgres
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
- **Docker volume**: `ordervschaos-data` (run `docker volume ls`)

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
