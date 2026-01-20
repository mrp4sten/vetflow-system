#!/bin/bash

# VetFlow Production Startup Script
# This script starts all services in production mode with optimized builds

echo "🐕 Starting VetFlow Production Environment..."
echo ""

# Check if .env exists
if [ ! -f .env ]; then
    echo "❌ .env file not found. Please create it from .env.example"
    exit 1
fi

# Validate required environment variables
if grep -q "your_secure_password" .env || grep -q "your-256-bit-secret" .env; then
    echo "⚠️  WARNING: Default passwords/secrets detected in .env"
    echo "   Please update POSTGRES_PASSWORD and VETFLO_JWT_SECRET before production use!"
    read -p "Continue anyway? (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

echo "🔨 Building production images..."
docker compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d

echo ""
echo "✅ VetFlow is running in production mode!"
echo ""
echo "📍 Access URL:"
echo "   http://localhost"
echo ""
echo "🔑 Default Login:"
echo "   Username: admin"
echo "   Password: Vetflow#2024"
echo ""
echo "📊 View logs:"
echo "   docker compose logs -f"
echo ""
echo "🛑 Stop services:"
echo "   docker compose down"
