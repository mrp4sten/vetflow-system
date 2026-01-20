#!/bin/bash

# VetFlow Development Startup Script

echo "🐕 VetFlow Development Environment Setup"
echo ""

# Check if .env exists
if [ ! -f .env ]; then
    echo "⚠️  .env file not found. Creating from .env.example..."
    cp .env.example .env
    echo "✅ Created .env file. Please update passwords and secrets before running again."
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

echo "Choose development setup:"
echo ""
echo "1) Backend + Database only (recommended - run frontend locally)"
echo "2) Full stack in Docker (may have permission issues)"
echo ""
read -p "Enter choice [1-2]: " choice

case $choice in
    1)
        echo ""
        echo "🔨 Starting database and backend API..."
        docker compose up postgres api --build
        echo ""
        echo "✅ Backend and database are running!"
        echo ""
        echo "📍 Access URLs:"
        echo "   - Backend:   http://localhost:8080"
        echo "   - Database:  localhost:5432"
        echo "   - Swagger:   http://localhost:8080/swagger-ui/index.html"
        echo ""
        echo "🚀 To run frontend locally:"
        echo "   cd ../frontend"
        echo "   npm install"
        echo "   npm run dev"
        echo ""
        echo "🔑 Default Login:"
        echo "   Username: admin"
        echo "   Password: Vetflow#2024"
        ;;
    2)
        echo ""
        echo "🔨 Building and starting all services..."
        docker compose up --build
        echo ""
        echo "🎉 VetFlow is running!"
        echo ""
        echo "📍 Access URLs:"
        echo "   - Frontend:  http://localhost:5173"
        echo "   - Backend:   http://localhost:8080"
        echo "   - Swagger:   http://localhost:8080/swagger-ui/index.html"
        echo ""
        echo "🔑 Default Login:"
        echo "   Username: admin"
        echo "   Password: Vetflow#2024"
        ;;
    *)
        echo "❌ Invalid choice. Exiting."
        exit 1
        ;;
esac

echo ""
echo "Press Ctrl+C to stop all services"
