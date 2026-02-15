# Disco.cloud Deployment Options for Order v Chaos

## Understanding Disco.cloud

Disco.cloud does **NOT support docker-compose**. Instead, it uses a `disco.json` file that defines services within a single project. Each service can be:
- A container with a Dockerfile
- A pre-built Docker image
- A cron job
- A deployment hook

## Our Challenge

Our app needs:
1. **PostgreSQL database** - persistent storage
2. **Kotlin app** - web service listening on port 8080

## Option 1: Separate Disco Projects (RECOMMENDED)

Deploy PostgreSQL and the app as **two separate Disco projects** on the same server.

### Advantages:
- ✅ Database persists independently of app deployments
- ✅ Can manage/restart services independently
- ✅ Database can be shared by multiple apps
- ✅ Clear separation of concerns

### How it works:

**Project 1: PostgreSQL Database**
```json
// disco.json for postgres project
{
  "version": "1.0",
  "services": {
    "web": {
      "image": "postgres:18",
      "port": 5432,
      "exposedInternally": true,
      "volumes": [
        {
          "name": "postgres-data",
          "destinationPath": "/var/lib/postgresql/data"
        }
      ]
    }
  }
}
```

**Project 2: Order v Chaos App**
```json
// disco.json for app project
{
  "version": "1.0",
  "services": {
    "web": {
      "port": 8080,
      "health": {
        "command": "curl -f http://localhost:8080/api/current || exit 1"
      }
    },
    "hook:deploy:start:before": {
      "type": "command",
      "command": "echo 'Flyway migrations run on app startup automatically'"
    }
  }
}
```

**Connection:** App connects to postgres via internal DNS (e.g., `postgres-project-name.local.disco`)

### Setup Steps:
1. Create postgres project first
2. Note the internal hostname (visible in Disco dashboard)
3. Create app project
4. Set DATABASE_URL environment variable in app project pointing to postgres hostname

---

## Option 2: Single Project with All-in-One Container

Use your existing `Dockerfile.single` which bundles PostgreSQL and the app.

### Advantages:
- ✅ Single deployment unit
- ✅ Simpler configuration
- ✅ No cross-service networking needed

### Disadvantages:
- ⚠️ Database data lost on redeploy unless volume is configured
- ⚠️ Can't restart app without restarting database
- ⚠️ Less scalable

### disco.json:
```json
{
  "version": "1.0",
  "services": {
    "web": {
      "port": 8080,
      "image": "app",
      "volumes": [
        {
          "name": "postgres-data",
          "destinationPath": "/var/lib/postgresql/14/main"
        }
      ],
      "health": {
        "command": "curl -f http://localhost:8080/api/current || exit 1"
      }
    }
  },
  "images": {
    "app": {
      "dockerfile": "Dockerfile.single",
      "context": "."
    }
  }
}
```

---

## Option 3: Embedded SQLite (NOT RECOMMENDED for this app)

Convert from PostgreSQL to SQLite for a simpler deployment.

### Why NOT recommended:
- ❌ Requires code changes (rewrite database layer)
- ❌ Less robust for concurrent writes
- ❌ No battle-tested migrations path
- ❌ App was designed for PostgreSQL

---

## RECOMMENDATION: Option 1 (Separate Projects)

This is the cleanest approach and follows best practices:

1. **Deploy PostgreSQL as a separate project**
   - One-time setup
   - Database survives app redeployments
   - Can be accessed by other projects if needed

2. **Deploy Order v Chaos as main project**
   - Uses existing Dockerfile
   - Configure DATABASE_URL via environment variables
   - Flyway migrations run automatically on startup

### Configuration Details

**PostgreSQL Project Setup:**
1. Create new Disco project named `ordervschaos-postgres`
2. Create minimal repo with just:
   - `disco.json` (see above)
   - `.gitignore`
3. Deploy to Disco
4. Set environment variables in Disco dashboard:
   - `POSTGRES_PASSWORD`: your_secure_password
   - `POSTGRES_DB`: ordervschaos
   - `POSTGRES_USER`: postgres

**App Project Setup:**
1. Create Disco project from your Order v Chaos repo
2. Set environment variables in Disco dashboard:
   - `DATABASE_URL`: `jdbc:postgresql://ordervschaos-postgres.local.disco:5432/ordervschaos`
   - `DATABASE_PASSWORD`: your_secure_password
   - `DATABASE_USER`: postgres
3. Push your code and deploy

### Important Notes

1. **Internal DNS:** Disco services can communicate via `[project-name].local.disco` hostname
2. **Ports:** PostgreSQL will be exposed internally but not to the public internet
3. **Volumes:** Data persists across deployments
4. **Migrations:** Flyway runs automatically when app starts (already in Application.kt)

---

## Environment Variables Reference

### For PostgreSQL Project
```
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password_here
POSTGRES_DB=ordervschaos
```

### For App Project
```
DATABASE_URL=jdbc:postgresql://ordervschaos-postgres.local.disco:5432/ordervschaos
DATABASE_USER=postgres
DATABASE_PASSWORD=your_secure_password_here
```

---

## Migration Strategy

If you already have data in a local PostgreSQL:

1. Dump local database:
   ```powershell
   $env:PGPASSWORD = "your_local_password"
   & "C:\Program Files\PostgreSQL\18\bin\pg_dump.exe" -U postgres ordervschaos > dump.sql
   ```

2. After deploying postgres project on Disco, SSH into your VPS:
   ```bash
   # Get container ID
   docker ps | grep postgres
   
   # Copy dump file
   scp dump.sql user@your-vps:/tmp/
   
   # Restore
   docker exec -i [container-id] psql -U postgres ordervschaos < /tmp/dump.sql
   ```

---

## Next Steps

1. Choose deployment option (recommend Option 1)
2. Create disco.json file for your chosen approach
3. Configure environment variables in Disco dashboard
4. Test deployment
5. Update submission with live URL
