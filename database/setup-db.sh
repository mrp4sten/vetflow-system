#!/bin/bash
# 
# setup-db.sh - Script to set up the VetFlow PostgreSQL database.
#
# Description: This script initializes the PostgreSQL database for the VetFlow system
# by executing the schema and seed data SQL files inside the PostgreSQL Docker container.
#
# Author: Mauricio Pasten (@mrp4sten)
# Created: 2025-10-01
# 
# Usage: ./setup-db.sh
# Ensure that the Docker container is running before executing this script.

echo "=== VetFlow Database Setup ==="

docker cp schema.sql vetflow_db:/tmp/schema.sql
docker cp seed-data.sql vetflow_db:/tmp/seed-data.sql

docker exec vetflow_db psql -U mrp4sten -d vetflow -f /tmp/schema.sql
docker exec vetflow_db psql -U mrp4sten -d vetflow -f /tmp/seed-data.sql

echo "=== Setup Complete ==="
echo "Verifying tables:"
docker exec vetflow_db psql -U mrp4sten -d vetflow -c "\dt"