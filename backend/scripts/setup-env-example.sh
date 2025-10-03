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

export SONAR_HOST_URL=""
export SONAR_TOKEN=""
export SONAR_PROJECT_KEY=""


echo "✅ Environment configured!"
echo "🌐 SonarQube URL: $SONAR_HOST_URL"
echo "🔑 Project: $SONAR_TOKEN"
echo "📊 Project Key: $SONAR_PROJECT_KEY"