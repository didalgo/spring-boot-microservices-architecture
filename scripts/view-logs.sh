#!/bin/bash

# ========================================
# View Logs Script
# ========================================

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;37m'
NC='\033[0m' # No Color

# Default values
SERVICE=""
TAIL=100
FOLLOW=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --service|-s)
            SERVICE="$2"
            shift 2
            ;;
        --tail|-t)
            TAIL="$2"
            shift 2
            ;;
        --follow|-f)
            FOLLOW=true
            shift
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --service, -s <name>    Service name to view logs"
            echo "  --tail, -t <number>     Number of lines to show (default: 100)"
            echo "  --follow, -f            Follow log output"
            echo "  --help, -h              Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                                  # List available services"
            echo "  $0 -s order-service                 # View last 100 lines"
            echo "  $0 -s order-service -f              # Follow logs"
            echo "  $0 -s api-gateway -t 500 -f         # View last 500 lines and follow"
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
echo -e "${CYAN}  Microservices Logs Viewer${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

if [ -z "$SERVICE" ]; then
    echo -e "${YELLOW}Available services:${NC}"
    docker-compose ps --services
    echo ""
    echo -e "${GRAY}Usage: $0 --service <service-name> [--tail 100] [--follow]${NC}"
    exit 0
fi

# Build command
cmd="docker-compose logs --tail=$TAIL"

if [ "$FOLLOW" = true ]; then
    cmd="$cmd -f"
fi

cmd="$cmd $SERVICE"

echo -e "${GREEN}Viewing logs for: $SERVICE${NC}"
echo -e "${GRAY}Command: $cmd${NC}"
echo ""

# Execute command
eval $cmd
