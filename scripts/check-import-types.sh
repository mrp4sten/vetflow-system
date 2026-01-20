#!/bin/bash
# VetFlow Frontend - Import Type Checker
# This script checks for TypeScript imports that should use 'import type'
# when verbatimModuleSyntax is enabled

set -e

FRONTEND_DIR="frontend/src"
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "================================================="
echo "  VetFlow - TypeScript Import Type Checker"
echo "================================================="
echo ""

# Check if frontend directory exists
if [ ! -d "$FRONTEND_DIR" ]; then
    echo -e "${RED}Error: frontend/src directory not found${NC}"
    echo "Please run this script from the project root directory"
    exit 1
fi

TOTAL_ISSUES=0

# 1. Check domain models
echo "1. Checking domain model imports..."
COUNT=$(grep -r "from '@domain/models/" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -v "import type" | grep -v "\.ts:import type" | wc -l)
if [ "$COUNT" -gt 0 ]; then
    echo -e "   ${RED}✗ Found $COUNT issues${NC}"
    grep -r "from '@domain/models/" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -v "import type" | grep -v "\.ts:import type" | head -5
    TOTAL_ISSUES=$((TOTAL_ISSUES + COUNT))
else
    echo -e "   ${GREEN}✓ No issues found${NC}"
fi

# 2. Check DTOs
echo ""
echo "2. Checking DTO imports..."
COUNT=$(grep -r "from '@application/dtos/" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -v "import type" | grep -v "\.dto\.ts:" | wc -l)
if [ "$COUNT" -gt 0 ]; then
    echo -e "   ${RED}✗ Found $COUNT issues${NC}"
    grep -r "from '@application/dtos/" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -v "import type" | grep -v "\.dto\.ts:" | head -5
    TOTAL_ISSUES=$((TOTAL_ISSUES + COUNT))
else
    echo -e "   ${GREEN}✓ No issues found${NC}"
fi

# 3. Check Axios types
echo ""
echo "3. Checking Axios type imports..."
COUNT=$(grep -r "from 'axios'" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -E "(AxiosError|AxiosInstance|AxiosRequestConfig|AxiosResponse)" | \
        grep -v "import type" | wc -l)
if [ "$COUNT" -gt 0 ]; then
    echo -e "   ${RED}✗ Found $COUNT issues${NC}"
    grep -r "from 'axios'" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -E "(AxiosError|AxiosInstance|AxiosRequestConfig|AxiosResponse)" | \
        grep -v "import type" | head -5
    TOTAL_ISSUES=$((TOTAL_ISSUES + COUNT))
else
    echo -e "   ${GREEN}✓ No issues found${NC}"
fi

# 4. Check repository interfaces
echo ""
echo "4. Checking repository interface imports..."
COUNT=$(grep -r "from '@domain/ports/repositories/" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -v "import type" | wc -l)
if [ "$COUNT" -gt 0 ]; then
    echo -e "   ${RED}✗ Found $COUNT issues${NC}"
    grep -r "from '@domain/ports/repositories/" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -v "import type" | head -5
    TOTAL_ISSUES=$((TOTAL_ISSUES + COUNT))
else
    echo -e "   ${GREEN}✓ No issues found${NC}"
fi

# 5. Check shared types
echo ""
echo "5. Checking shared type imports..."
COUNT=$(grep -r "from '@shared/types/" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -v "import type" | wc -l)
if [ "$COUNT" -gt 0 ]; then
    echo -e "   ${RED}✗ Found $COUNT issues${NC}"
    grep -r "from '@shared/types/" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -v "import type" | head -5
    TOTAL_ISSUES=$((TOTAL_ISSUES + COUNT))
else
    echo -e "   ${GREEN}✓ No issues found${NC}"
fi

# 6. Check use case interfaces
echo ""
echo "6. Checking use case interface imports..."
COUNT=$(grep -r "UseCase } from '@domain/use-cases/" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -v "import type" | wc -l)
if [ "$COUNT" -gt 0 ]; then
    echo -e "   ${RED}✗ Found $COUNT issues${NC}"
    grep -r "UseCase } from '@domain/use-cases/" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep -v "import type" | head -5
    TOTAL_ISSUES=$((TOTAL_ISSUES + COUNT))
else
    echo -e "   ${GREEN}✓ No issues found${NC}"
fi

# 7. Check for mixed class/interface imports (PaginatedResponse)
echo ""
echo "7. Checking for mixed BaseApiService/PaginatedResponse imports..."
COUNT=$(grep -r "BaseApiService, PaginatedResponse" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | wc -l)
if [ "$COUNT" -gt 0 ]; then
    echo -e "   ${RED}✗ Found $COUNT issues${NC}"
    grep -r "BaseApiService, PaginatedResponse" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | head -5
    echo -e "   ${YELLOW}Hint: Separate into two imports:${NC}"
    echo -e "   ${GREEN}import { BaseApiService } from './BaseApiService'${NC}"
    echo -e "   ${GREEN}import type { PaginatedResponse } from './BaseApiService'${NC}"
    TOTAL_ISSUES=$((TOTAL_ISSUES + COUNT))
else
    echo -e "   ${GREEN}✓ No issues found${NC}"
fi

# 8. Check for mixed schema/type imports (Zod inferred types)
echo ""
echo "8. Checking for mixed schema/FormData imports..."
COUNT=$(grep -r "Schema,.*FormData\|Schema,.*Data }" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep "from '@shared/schemas/" | grep -v "import type" | wc -l)
if [ "$COUNT" -gt 0 ]; then
    echo -e "   ${RED}✗ Found $COUNT issues${NC}"
    grep -r "Schema,.*FormData\|Schema,.*Data }" "$FRONTEND_DIR" --include="*.ts" --include="*.tsx" 2>/dev/null | \
        grep "from '@shared/schemas/" | grep -v "import type" | head -5
    echo -e "   ${YELLOW}Hint: Separate schema (value) from FormData (type):${NC}"
    echo -e "   ${GREEN}import { loginSchema } from '@shared/schemas/auth.schema'${NC}"
    echo -e "   ${GREEN}import type { LoginFormData } from '@shared/schemas/auth.schema'${NC}"
    TOTAL_ISSUES=$((TOTAL_ISSUES + COUNT))
else
    echo -e "   ${GREEN}✓ No issues found${NC}"
fi

# Summary
echo ""
echo "================================================="
echo "  Summary"
echo "================================================="

if [ "$TOTAL_ISSUES" -eq 0 ]; then
    echo -e "${GREEN}✅ All import type checks passed!${NC}"
    echo ""
    echo "No import type issues found. Your codebase is compliant"
    echo "with TypeScript's verbatimModuleSyntax setting."
    exit 0
else
    echo -e "${RED}⚠️  Found $TOTAL_ISSUES total issues${NC}"
    echo ""
    echo "Fix these issues by changing:"
    echo "  ${YELLOW}import { Type } from 'module'${NC}"
    echo "to:"
    echo "  ${GREEN}import type { Type } from 'module'${NC}"
    echo ""
    echo "For types, interfaces, and type-only imports."
    exit 1
fi
