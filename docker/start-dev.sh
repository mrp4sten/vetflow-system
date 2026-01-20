#!/bin/bash

# VetFlow Development Startup Script
# This script starts all services in development mode with hot reload

echo "🐕 Starting VetFlow Development Environment..."
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

echo "🔨 Building and starting services..."
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
echo ""
echo "Press Ctrl+C to stop all services"
