#!/bin/bash
#
# setup-env.sh - Setup environment variables
#
# Description: This script sets up environment variables for the application.
# Author: Mauricio Pasten (@mrp4sten)
# Version: 1.0.0
# Created: 2025-10-01
#
# Usage: source ./setup-env.sh
# Notes: Run this script before starting the application to ensure all environment variables are set.
#

echo "🔧 Setting up environment variables..."

# SonarQube configuration
export SONAR_HOST_URL=""
export SONAR_TOKEN=""
export SONAR_PROJECT_KEY=""

# API configuration
export DB_URL=""
export DB_USERNAME=""
export DB_PASSWORD=""
export SERVER_PORT=""

# CORS
export VETFLO_ALLOWED_ORIGINS=""

# Security
export VETFLO_JWT_SECRET=""

echo "✅ Environment configured!"
echo "================================="
echo "🌐 SonarQube URL: $SONAR_HOST_URL"
echo "📊 Project Key: $SONAR_PROJECT_KEY"
echo "🔑 Project: $SONAR_TOKEN"
echo "================================="
echo "🗄️  Database URL: $DB_URL"
echo "👤 Database User: $DB_USERNAME"
echo "🔑 Database Password: $DB_PASSWORD"
echo "🌐 Server Port: $SERVER_PORT"