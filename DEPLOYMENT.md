# Deployment Guide Summary

## Local Development (Recommended)

### Start PostgreSQL
```powershell
docker run -d `
  --name ordervschaos-postgres `
  -e POSTGRES_PASSWORD=postgres `
  -e POSTGRES_DB=ordervschaos `
  -p 5432:5432 `
  -v ordervschaos-data:/var/lib/postgresql/data `
  postgres:18
```

### Run Application
```powershell
$env:DATABASE_PASSWORD = "postgres"
.\gradlew.bat run
```

**URL**: http://localhost:8080

---

## Docker Development (Matches Production)

### PostgreSQL Container
```powershell
docker run -d `
  --name ordervschaos-postgres `
  -e POSTGRES_PASSWORD=postgres `
  -e POSTGRES_DB=ordervschaos `
  -p 5432:5432 `
  -v ordervschaos-data:/var/lib/postgresql/data `
  postgres:18
```

### Build & Run App
```powershell
docker build -t ordervschaos .
docker run -d `
  --name ordervschaos `
  -p 8080:8080 `
  -e DATABASE_URL="jdbc:postgresql://ordervschaos-postgres:5432/ordervschaos" `
  -e DATABASE_PASSWORD="postgres" `
  --link ordervschaos-postgres:ordervschaos-postgres `
  ordervschaos
```

**URL**: http://localhost:8080

---

## Disco.cloud Production

### 1. Deploy PostgreSQL (separate project)
Create new repo with:
```json
{
  "version": "1.0",
  "services": {
    "web": {
      "image": "postgres:18",
      "port": 5432,
      "exposedInternally": true,
      "volumes": [{
        "name": "postgres-data",
        "destinationPath": "/var/lib/postgresql/data"
      }]
    }
  }
}
```

Set env vars in Disco dashboard:
- `POSTGRES_PASSWORD=your_secure_password`
- `POSTGRES_DB=ordervschaos`

### 2. Deploy App (this repo)
Set env vars in Disco dashboard:
- `DATABASE_URL=jdbc:postgresql://postgres-project.local.disco:5432/ordervschaos`
- `DATABASE_PASSWORD=your_secure_password`

The `disco.json` in this repo is already configured.

---

## Why This Architecture?

1. **Disco.cloud native**: No docker-compose support, so we design for separate services
2. **Development/production parity**: Same pattern locally and in production
3. **Database persistence**: PostgreSQL data survives app redeployments
4. **Flexibility**: Easy to swap PostgreSQL for managed database service
5. **Simplicity**: Single Dockerfile, straightforward configuration

---

See full documentation:
- `README.md` - Main documentation
- `QUICKSTART.md` - Quick command reference
- `docs/DISCO_DEPLOYMENT.md` - Complete disco.cloud guide
- `docs/DOCKER.md` - Docker details (optional reading)
