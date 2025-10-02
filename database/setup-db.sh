#!/bin/bash
#
# setup-db.sh - VetFlow Database Initialization Script
# 
# 🐾 Purpose: Deploy database schema and seed data like a pro
# 💊 Author: Mauricio Pasten (@MrP4sten) - The Code Dealer
# 🚀 Version: 1.0 - "The MyDrugs of Database Scripts"
#
# Usage: ./setup-db.sh
# Prerequisites: Docker container 'vetflow_db' running

echo "🔄 VetFlow Database Setup - Starting Operation..."
echo "================================================"

# Check if Docker container is running
echo "🔍 Checking if vetflow_db container is alive..."
if ! docker ps | grep -q vetflow_db; then
    echo "❌ ERROR: vetflow_db container not found!"
    echo "💡 Tip: Run 'docker-compose up -d postgres' first"
    exit 1
fi

echo "✅ Container found! Proceeding with database setup..."
echo ""

# Copy SQL files to container
echo "📤 Uploading schema to container..."
docker cp schema.sql vetflow_db:/tmp/schema.sql

echo "🌱 Uploading seed data..."
docker cp seed-data.sql vetflow_db:/tmp/seed-data.sql

echo ""
echo "🚀 Deploying database components..."

# Execute schema
echo "📊 Creating database schema..."
if docker exec vetflow_db psql -U mrp4sten -d vetflow -f /tmp/schema.sql; then
    echo "✅ Schema deployed successfully!"
else
    echo "❌ Schema deployment failed!"
    exit 1
fi

# Execute seed data
echo "🎯 Loading sample data..."
if docker exec vetflow_db psql -U mrp4sten -d vetflow -f /tmp/seed-data.sql; then
    echo "✅ Seed data loaded successfully!"
else
    echo "❌ Seed data loading failed!"
    exit 1
fi

echo ""
echo "================================================"
echo "🎉 VETFLOW DATABASE SETUP COMPLETE!"
echo "================================================"

# Final verification
echo "🔍 Running final verification..."
docker exec vetflow_db psql -U mrp4sten -d vetflow -c "
SELECT '🐕 ' || COUNT(*) || ' patients' FROM patients
UNION ALL SELECT '👨‍💼 ' || COUNT(*) || ' owners' FROM owners  
UNION ALL SELECT '📅 ' || COUNT(*) || ' appointments' FROM appointments
UNION ALL SELECT '🏥 ' || COUNT(*) || ' medical records' FROM medical_records
UNION ALL SELECT '🔐 ' || COUNT(*) || ' system users' FROM system_users;"

echo ""
echo "💡 Next steps:"
echo "   Connect: docker exec -it vetflow_db psql -U mrp4sten -d vetflow"
echo "   View logs: docker-compose logs -f postgres"
echo "   Stop: docker-compose down"
echo ""
echo "🚀 Your VetFlow database is ready for action!"