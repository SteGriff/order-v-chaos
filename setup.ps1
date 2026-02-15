# Order v Chaos - Setup Script (PowerShell)

Write-Host "========================================"
Write-Host "Order v Chaos - Setup Helper"
Write-Host "========================================"
Write-Host ""

# Set JAVA_HOME to IntelliJ's JBR (or find it)
$javaPath = "C:\Users\$env:USERNAME\AppData\Local\Programs\IntelliJ IDEA\jbr"
if (Test-Path $javaPath) {
    $env:JAVA_HOME = $javaPath
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    Write-Host "[1/4] Java configured"
    java -version
} else {
    Write-Host "[1/4] WARNING: JetBrains JDK not found at expected location"
    Write-Host "      Please set JAVA_HOME manually or install JDK 21+"
    Write-Host "      Expected: $javaPath"
}
Write-Host ""

# Check for PostgreSQL
Write-Host "[2/4] Checking for PostgreSQL..."
$pgPath = "C:\Program Files\PostgreSQL\18\bin"
if (Test-Path $pgPath) {
    $env:PATH = "$pgPath;$env:PATH"
    Write-Host "PostgreSQL found!"
    Write-Host ""
    Write-Host "To create the database, run:"
    Write-Host '  $env:PGPASSWORD = "your_password"'
    Write-Host '  & "C:\Program Files\PostgreSQL\18\bin\createdb.exe" -U postgres ordervschaos'
} else {
    Write-Host "WARNING: PostgreSQL not found at expected location"
    Write-Host ""
    Write-Host "Please install PostgreSQL from: https://www.postgresql.org/download/windows/"
    Write-Host "Or if already installed, add it to your PATH"
    Write-Host ""
    Write-Host "After installing PostgreSQL, create the database:"
    Write-Host "  psql -U postgres"
    Write-Host "  CREATE DATABASE ordervschaos;"
}
Write-Host ""

Write-Host "[3/4] Database Configuration"
Write-Host "Set your PostgreSQL password:"
Write-Host '  $env:DATABASE_PASSWORD = "your_password_here"'
Write-Host ""
Write-Host "Default configuration (edit src\main\resources\application.conf if different):"
Write-Host "  jdbcUrl = jdbc:postgresql://localhost:5432/ordervschaos"
Write-Host "  user = postgres"
Write-Host "  password = (from DATABASE_PASSWORD environment variable)"
Write-Host ""

Write-Host "[4/4] Ready to run!"
Write-Host ""
Write-Host "To start the application:"
Write-Host '  $env:DATABASE_PASSWORD = "your_password"'
Write-Host "  .\gradlew.bat run"
Write-Host ""
Write-Host "Then open: http://localhost:8080"
Write-Host ""
Write-Host "========================================"
Write-Host ""
Read-Host "Press Enter to continue"
