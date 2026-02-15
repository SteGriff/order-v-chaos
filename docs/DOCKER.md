# Docker Deployment Guide

## Two Deployment Options

### Option 1: Separate Containers (RECOMMENDED) ⭐

**Pros:**
- ✅ Industry standard approach
- ✅ Easy to scale app independently
- ✅ Can restart app without affecting database  
- ✅ Database data persists safely in Docker volume
- ✅ Can easily upgrade PostgreSQL version
- ✅ Simpler debugging and logs
- ✅ Can run multiple app instances with load balancer

**Cons:**
- Slightly more complex initial setup (but docker-compose makes it easy)
- Two containers to manage (automated with docker-compose)

### Option 2: Single Container (SIMPLER BUT NOT RECOMMENDED)

**Pros:**
- ✅ One container to manage
- ✅ Simpler deployment (just one `docker run` command)
- ✅ Good for demos or very constrained environments

**Cons:**
- ❌ Against Docker best practices
- ❌ Can't scale app without database
- ❌ Restarting app restarts database too
- ❌ More complex to debug
- ❌ Larger image size
- ❌ Risk of data loss

---

## Recommended: Separate Containers (Docker Compose)

### Quick Start

1. **Create `.env` file**:
   ```powershell
   Copy-Item .env.example .env
   # Edit .env and set your DATABASE_PASSWORD
   ```

2. **Build and run**:
   ```powershell
   docker-compose up --build
   ```

3. **Access the application**:
   - App: http://localhost:8080
   - Database: localhost:5432

4. **Stop the application**:
   ```powershell
   docker-compose down
   ```

5. **Stop and remove data** (careful!):
   ```powershell
   docker-compose down -v
   ```

### Production Deployment

```powershell
# Set production password in .env
"DATABASE_PASSWORD=your_secure_password" | Out-File -FilePath .env -Encoding ASCII

# Run in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Check status
docker-compose ps
```

### Docker Commands

```powershell
# Build without cache
docker-compose build --no-cache

# Rebuild and restart
docker-compose up --build -d

# View logs (all services)
docker-compose logs -f

# View logs (specific service)
docker-compose logs -f app
docker-compose logs -f postgres

# Execute commands in running container
docker-compose exec app sh
docker-compose exec postgres psql -U postgres -d ordervschaos

# Stop services
docker-compose stop

# Remove containers (keeps volumes)
docker-compose down

# Remove everything including volumes
docker-compose down -v
```

---

## Alternative: Single Container

If you really need everything in one container:

### Build

First, build the application JAR:
```powershell
.\gradlew.bat build
```

Then build the Docker image:
```powershell
docker build -f Dockerfile.single -t ordervschaos-single .
```

### Run

```powershell
docker run -d `
  --name ordervschaos `
  -p 8080:8080 `
  -e DATABASE_PASSWORD=your_password `
  ordervschaos-single
```

### Manage

```powershell
# View logs
docker logs -f ordervschaos

# Stop
docker stop ordervschaos

# Start
docker start ordervschaos

# Remove
docker rm -f ordervschaos
```

**⚠️ Warning**: With single container, if you remove the container, **all database data is lost**!

---

## Architecture Comparison

### Separate Containers (Recommended)
```
┌─────────────────────────────────────┐
│   Docker Compose Network            │
│                                      │
│  ┌────────────────┐                 │
│  │   postgres     │                 │
│  │   (port 5432)  │                 │
│  │   Volume:      │                 │
│  │   postgres_data│ ← Persists!     │
│  └────────┬───────┘                 │
│           │                          │
│           │ JDBC connection          │
│           │                          │
│  ┌────────▼───────┐                 │
│  │   app          │                 │
│  │   (port 8080)  │                 │
│  └────────────────┘                 │
└─────────────────────────────────────┘
```

### Single Container
```
┌─────────────────────────────────────┐
│   ordervschaos-single               │
│                                      │
│  ┌────────────────┐                 │
│  │  Supervisor    │                 │
│  │                │                 │
│  │  ┌──────────┐  │                 │
│  │  │PostgreSQL│  │                 │
│  │  └─────┬────┘  │                 │
│  │        │       │                 │
│  │  ┌─────▼────┐  │                 │
│  │  │   App    │  │                 │
│  │  └──────────┘  │                 │
│  └────────────────┘                 │
└─────────────────────────────────────┘
   ⚠️ Data lost if container removed!
```

## Environment Variables

Both options support:
- `DATABASE_URL` - JDBC connection string
- `DATABASE_USER` - Database username  
- `DATABASE_PASSWORD` - Database password
- `PORT` - Server port (default: 8080)

## Troubleshooting

### App can't connect to database
```powershell
# With docker-compose
docker-compose logs postgres
docker-compose exec app ping postgres

# With single container
docker logs ordervschaos
```

### Database data lost
- **Docker Compose**: Use volumes (already configured)
- **Single Container**: Consider using a Docker volume:
  ```powershell
  docker run -d `
    -v ordervschaos-data:/var/lib/postgresql/14/main `
    --name ordervschaos `
    -p 8080:8080 `
    ordervschaos-single
  ```

### Port already in use
Change port mapping:
```powershell
# Docker Compose: Edit docker-compose.yml
ports: - "8081:8080"

# Single Container:
docker run -p 8081:8080 ...
```

## Backup and Restore

### Docker Compose
```powershell
# Backup
docker-compose exec postgres pg_dump -U postgres ordervschaos > backup.sql

# Restore
Get-Content backup.sql | docker-compose exec -T postgres psql -U postgres ordervschaos
```

### Single Container
```powershell
# Backup
docker exec ordervschaos sudo -u postgres pg_dump ordervschaos > backup.sql

# Restore
Get-Content backup.sql | docker exec -i ordervschaos sudo -u postgres psql ordervschaos
```

## Recommendation

**Use Docker Compose** (separate containers) unless you have a very specific reason not to. It's the industry standard for a reason!
