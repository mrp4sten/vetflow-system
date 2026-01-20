#!/bin/bash

# VetFlow Local Development Startup Script
# This script helps you start services locally

echo "🐕 VetFlow Local Development Setup"
echo ""

# Check if in correct directory
if [ ! -f "docker/docker-compose.yml" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    exit 1
fi

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install Java 17+"
    exit 1
fi

# Check Node
if ! command -v node &> /dev/null; then
    echo "❌ Node.js not found. Please install Node.js 18+"
    exit 1
fi

echo "✅ Prerequisites check passed"
echo ""

# Start PostgreSQL in Docker
echo "📦 Starting PostgreSQL in Docker..."
cd docker
docker compose up postgres -d

echo ""
echo "⏳ Waiting for PostgreSQL to be healthy..."
for i in {1..30}; do
    if docker compose ps postgres | grep -q "healthy"; then
        echo "✅ PostgreSQL is healthy!"
        break
    fi
    sleep 1
    echo -n "."
done

cd ..

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Database is ready!"
echo ""
echo "📝 Next steps - Open 2 new terminals and run:"
echo ""
echo "🔷 Terminal 1 (Backend):"
echo "   cd backend"
echo "   ./mvnw spring-boot:run"
echo ""
echo "🔷 Terminal 2 (Frontend):"
echo "   cd frontend"
echo "   npm install    # (first time only)"
echo "   npm run dev"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📍 Access URLs:"
echo "   - Frontend:  http://localhost:3000"
echo "   - Backend:   http://localhost:8080"
echo "   - Swagger:   http://localhost:8080/swagger-ui/index.html"
echo ""
echo "🔑 Login:"
echo "   Username: admin"
echo "   Password: Vetflow#2024"
echo ""
echo "🛑 To stop PostgreSQL:"
echo "   cd docker && docker compose down"
