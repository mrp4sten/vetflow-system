# VetFlow - Complete Setup Guide

## 🏗️ Architecture Overview

VetFlow is a full-stack veterinary clinic management system with:

- **Backend:** Spring Boot 3.5 (Java 17) REST API
- **Frontend:** React 18 + TypeScript + Vite
- **Database:** PostgreSQL 15
- **Containerization:** Docker + Docker Compose

## 📋 Prerequisites

Before you begin, ensure you have installed:

- **Docker** (v20.10+) and **Docker Compose** (v2.0+)
- **Git** (v2.0+)
- *Optional:* Node.js 18+ and Java 17+ for local development without Docker

## 🚀 Quick Start (Docker - Recommended)

### 1. Clone the Repository

```bash
git clone git@github.com:mrp4sten/vetflow-system.git
cd vetflow-system
```

### 2. Configure Environment Variables

```bash
cd docker
cp .env.example .env
```

**Edit `.env` file** and update the following:

```bash
# Required: Change these in production
POSTGRES_PASSWORD=your_secure_password
VETFLO_JWT_SECRET=your-256-bit-secret-key
```

### 3. Start the Full Stack

```bash
# Start all services (database + backend + frontend)
docker compose up --build

# Or run in detached mode
docker compose up -d --build
```

**Services will be available at:**
- 🖥️ **Frontend:** http://localhost:5173
- 🔌 **Backend API:** http://localhost:8080
- 🗄️ **Database:** localhost:5432
- 📚 **API Docs (Swagger):** http://localhost:8080/swagger-ui/index.html

### 4. Default Admin Login

Once the application is running, log in with:

```
Username: admin
Password: Vetflow#2024
```

### 5. Stop the Services

```bash
docker compose down

# To also remove volumes (deletes database data)
docker compose down -v
```

---

## 🛠️ Development Setup

### Option A: Full Docker Development (Hot Reload Enabled)

The default `docker-compose.yml` is configured for development with hot reload:

```bash
cd docker
docker compose up
```

**Features:**
- ✅ Frontend hot reload (changes in `frontend/src` auto-refresh)
- ✅ Backend auto-restart on file changes
- ✅ Source code mounted as volumes
- ✅ All services networked together

### Option B: Hybrid Development (Database in Docker, Services Local)

#### 1. Start only the database

```bash
cd docker
docker compose up postgres -d
```

#### 2. Run Backend Locally

```bash
cd backend
./mvnw spring-boot:run
```

Backend will run on http://localhost:8080

#### 3. Run Frontend Locally

```bash
cd frontend
npm install
npm run dev
```

Frontend will run on http://localhost:5173

### Option C: Production Build (Nginx + Optimized)

To build and run production images:

```bash
cd docker
# Update .env to use production Dockerfile
echo "FRONTEND_DOCKERFILE=Dockerfile" >> .env

docker compose up --build
```

**Production features:**
- ✅ Optimized React build
- ✅ Nginx server with gzip compression
- ✅ Static asset caching
- ✅ Security headers

---

## 📂 Project Structure

```
vetflow-system/
├── backend/               # Spring Boot API
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   └── README.md
├── frontend/              # React + TypeScript app
│   ├── src/
│   ├── package.json
│   ├── Dockerfile         # Production build
│   ├── Dockerfile.dev     # Development with hot reload
│   ├── nginx.conf
│   └── README.md
├── database/              # PostgreSQL schemas & migrations
│   └── README.md
├── docker/                # Docker Compose configuration
│   ├── docker-compose.yml
│   ├── .env
│   └── .env.example
├── docs/                  # Additional documentation
└── README.md
```

---

## 🌳 Git Branches Explained

### **Understanding the Repository:**

This is **ONE repository** with multiple branches for different features:

```
vetflow-system (one repo)
├── .git/                  # Git metadata (tracks all branches)
├── master                 # Main production-ready branch ⭐
├── frontend-development   # Active frontend development
├── feature/backend-development
└── feature/database-design
```

### **Available Branches:**

| Branch | Purpose | Status |
|--------|---------|--------|
| `master` | Main production branch | ✅ Active |
| `frontend-development` | Frontend features | ✅ Active |
| `feature/backend-development` | Backend API development | ✅ Merged to master |
| `feature/database-design` | Database schema design | ✅ Merged to master |

### **Working with Branches:**

```bash
# List all branches
git branch -a

# Switch to a branch
git checkout frontend-development

# Create new feature branch
git checkout -b feature/my-new-feature

# Push to remote
git push origin feature/my-new-feature
```

**Important:** When you switch branches, ALL folders (frontend/, backend/, etc.) update to that branch's version.

---

## 🔧 Configuration Details

### Environment Variables Reference

#### Database Configuration
```bash
POSTGRES_DB=vetflow              # Database name
POSTGRES_USER=mrp4sten           # Database user
POSTGRES_PASSWORD=your_password  # Database password (CHANGE THIS!)
```

#### Backend Configuration
```bash
VETFLO_JWT_SECRET=your-secret-key           # JWT signing key (256-bit)
VETFLO_ALLOWED_ORIGINS=http://localhost:5173 # CORS origins
```

#### Frontend Configuration
```bash
VITE_API_BASE_URL=http://localhost:8080/api/v1  # Backend API URL
FRONTEND_PORT=5173                               # External port
FRONTEND_INTERNAL_PORT=5173                      # Container port
FRONTEND_DOCKERFILE=Dockerfile.dev               # Dev or Prod
```

### Port Mapping

| Service | Host Port | Container Port | URL |
|---------|-----------|----------------|-----|
| Frontend | 5173 | 5173 | http://localhost:5173 |
| Backend | 8080 | 8080 | http://localhost:8080 |
| Database | 5432 | 5432 | localhost:5432 |

---

## 🧪 Testing the Setup

### 1. Check Services are Running

```bash
docker compose ps
```

Expected output:
```
NAME                 IMAGE              STATUS
vetflow_db           postgres:15        Up (healthy)
vetflow_api          vetflow-backend    Up
vetflow_frontend     vetflow-frontend   Up
```

### 2. Test Backend API

```bash
# Health check
curl http://localhost:8080/actuator/health

# Get JWT token
curl -X POST http://localhost:8080/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Vetflow#2024"}'
```

### 3. Test Frontend

Open browser: http://localhost:5173
- Should see VetFlow login page
- Login with `admin` / `Vetflow#2024`
- Navigate to Dashboard

### 4. Check Database

```bash
# Connect to database
docker exec -it vetflow_db psql -U mrp4sten -d vetflow

# Inside psql:
\dt                    # List tables
SELECT * FROM system_users;
\q                     # Quit
```

---

## 🐛 Troubleshooting

### Frontend can't connect to Backend

**Problem:** `ERR_CONNECTION_REFUSED` or CORS errors

**Solution:**
1. Check `VITE_API_BASE_URL` in `.env` matches backend URL
2. Verify `VETFLO_ALLOWED_ORIGINS` includes frontend URL
3. Ensure backend is running: `docker compose logs api`

### Database connection errors

**Problem:** Backend fails with `Connection refused`

**Solution:**
1. Wait for database health check: `docker compose ps`
2. Check database logs: `docker compose logs postgres`
3. Verify credentials in `.env` match between services

### Port already in use

**Problem:** `Error: port is already allocated`

**Solution:**
```bash
# Find process using port 5173
lsof -i :5173
kill -9 <PID>

# Or change port in .env
FRONTEND_PORT=3000
```

### Hot reload not working

**Problem:** Changes to code don't reflect in browser

**Solution:**
1. Ensure volume mounts are correct in `docker-compose.yml`
2. Try rebuilding: `docker compose up --build`
3. Check frontend logs: `docker compose logs frontend`

### Database data persists after `down`

**Problem:** Old data remains after restart

**Solution:**
```bash
# Remove volumes to start fresh
docker compose down -v
docker compose up --build
```

---

## 📚 Additional Resources

- **Backend README:** `backend/README.md`
- **Frontend README:** `frontend/README.md`
- **Database README:** `database/README.md`
- **API Documentation:** http://localhost:8080/swagger-ui/index.html
- **Project Guide:** `AGENTS.md` (for AI assistants)

---

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/amazing-feature`
2. Make your changes
3. Commit: `git commit -m 'feat: add amazing feature'`
4. Push: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## 📝 Notes

- **Default user:** The system creates an admin user automatically via Flyway migration
- **JWT Secret:** MUST be changed in production (256-bit recommended)
- **Database:** Uses Flyway for migrations (automatic on startup)
- **CORS:** Frontend URL must be in `VETFLO_ALLOWED_ORIGINS`

---

## 🆘 Getting Help

If you encounter issues:

1. Check logs: `docker compose logs <service-name>`
2. Review this guide's troubleshooting section
3. Check individual service READMEs
4. Open an issue on GitHub

---

**Last Updated:** January 19, 2026
**Version:** v0.11.0
