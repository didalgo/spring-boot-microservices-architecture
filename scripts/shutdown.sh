#!/bin/bash

# ========================================
# Microservices Shutdown Script
# ========================================

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Parse arguments
VOLUMES=false
IMAGES=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --volumes|-v)
            VOLUMES=true
            shift
            ;;
        --images|-i)
            IMAGES=true
            shift
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --volumes, -v    Remove volumes (data will be lost)"
            echo "  --images, -i     Remove images"
            echo "  --help, -h       Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}  Microservices Architecture Shutdown${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

echo -e "${YELLOW}Stopping services...${NC}"

if [ "$VOLUMES" = true ]; then
    echo -e "${YELLOW}Removing volumes...${NC}"
    docker-compose down -v
elif [ "$IMAGES" = true ]; then
    echo -e "${YELLOW}Removing images...${NC}"
    docker-compose down --rmi all
else
    docker-compose down
fi

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Services stopped successfully${NC}"
else
    echo -e "${RED}ERROR: Failed to stop services${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Shutdown complete${NC}"
echo -e "${GREEN}========================================${NC}"
