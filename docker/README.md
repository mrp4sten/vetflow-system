# VetFlow Docker Setup

## Quick Start

### Option 1: Database + Backend Only (Recommended for Frontend Development)

If you're actively developing the frontend, it's easier to run it locally:

```bash
# Start database and backend API
docker compose up postgres api -d

# In another terminal, run frontend locally
cd ../frontend
npm install
npm run dev
```

**Benefits:**
- No permission issues with mounted volumes
- Faster hot reload
- Better IDE integration
- Direct access to node_modules

**Access:**
- Frontend: http://localhost:5173 (local Vite)
- Backend: http://localhost:8080
- Database: localhost:5432

### Option 2: Full Stack with Docker

If you want everything in Docker:

```bash
# Stop any running containers
docker compose down

# Start all services
docker compose up --build

# Or use the convenience script
./start-dev.sh
```

**Note:** The frontend service uses volume mounts which may have permission issues on some systems. If you encounter `EACCES` errors, use Option 1 instead.

## Available Services

| Service | Container | Port | Description |
|---------|-----------|------|-------------|
| Database | `vetflow_db` | 5432 | PostgreSQL 15 |
| Backend | `vetflow_api` | 8080 | Spring Boot API |
| Frontend | `vetflow_frontend` | 5173 | React + Vite (dev mode) |

## Environment Variables

Copy `.env.example` to `.env` and update:

```bash
cp .env.example .env
```

**Required variables:**
- `POSTGRES_PASSWORD` - Database password
- `VETFLO_JWT_SECRET` - JWT signing key (256-bit)

## Common Commands

```bash
# Start all services
docker compose up

# Start in background
docker compose up -d

# Stop all services
docker compose down

# Stop and remove volumes (fresh start)
docker compose down -v

# View logs
docker compose logs -f

# View logs for specific service
docker compose logs -f api

# Rebuild a specific service
docker compose up --build frontend

# Access database
docker exec -it vetflow_db psql -U mrp4sten -d vetflow
```

## Production Deployment

For production, use the production override:

```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d
```

Or use the script:

```bash
./start-prod.sh
```

## Troubleshooting

### Frontend Permission Issues

**Problem:** `EACCES: permission denied` errors in frontend container

**Solution:** Run frontend locally instead:
```bash
docker compose up postgres api -d
cd ../frontend && npm run dev
```

### Database Connection Failed

**Problem:** Backend can't connect to database

**Solutions:**
1. Wait for database health check: `docker compose ps`
2. Check credentials in `.env` match between services
3. Restart services: `docker compose restart`

### Port Already in Use

**Problem:** `port is already allocated`

**Solution:**
```bash
# Find process using the port
lsof -i :5173
# Kill it
kill -9 <PID>
# Or change port in .env
```

## See Also

- [Complete Setup Guide](../SETUP.md)
- [Backend README](../backend/README.md)
- [Frontend README](../frontend/README.md)
