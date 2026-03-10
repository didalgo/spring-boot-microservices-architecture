#!/bin/bash

# ========================================
# Microservices Startup Script
# ========================================

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;37m'
NC='\033[0m' # No Color

# Parse arguments
BUILD=false
CLEAN=false
VERBOSE=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --build|-b)
            BUILD=true
            shift
            ;;
        --clean|-c)
            CLEAN=true
            shift
            ;;
        --verbose|-v)
            VERBOSE=true
            shift
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --build, -b      Build images before starting"
            echo "  --clean, -c      Clean up old containers and volumes"
            echo "  --verbose, -v    Show logs in foreground"
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
echo -e "${CYAN}  Microservices Architecture Startup${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# Check Docker
echo -e "${YELLOW}Checking Docker...${NC}"
if ! docker --version &> /dev/null; then
    echo -e "${RED}ERROR: Docker is not installed or not running!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker is running${NC}"
echo ""

# Clean if requested
if [ "$CLEAN" = true ]; then
    echo -e "${YELLOW}Cleaning up old containers and volumes...${NC}"
    docker-compose down -v
    echo -e "${GREEN}✓ Cleanup complete${NC}"
    echo ""
fi

# Build if requested
if [ "$BUILD" = true ]; then
    echo -e "${YELLOW}Building all images...${NC}"
    docker-compose build
    if [ $? -ne 0 ]; then
        echo -e "${RED}ERROR: Build failed!${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Build complete${NC}"
    echo ""
fi

# Start services
echo -e "${YELLOW}Starting services...${NC}"
if [ "$VERBOSE" = true ]; then
    docker-compose up
else
    docker-compose up -d
    echo -e "${GREEN}✓ Services started in background${NC}"
    echo ""
    
    # Wait for services to be healthy
    echo -e "${YELLOW}Waiting for services to be healthy...${NC}"
    sleep 5
    
    max_attempts=60
    attempt=0
    all_healthy=false
    
    while [ $attempt -lt $max_attempts ] && [ "$all_healthy" = false ]; do
        attempt=$((attempt + 1))
        echo -e "${GRAY}Attempt $attempt/$max_attempts...${NC}"
        
        # Check health status
        unhealthy=$(docker-compose ps --format json 2>/dev/null | jq -r 'select(.Health != "healthy" and .Service != "zookeeper") | .Service' 2>/dev/null | wc -l)
        
        if [ "$unhealthy" -eq 0 ]; then
            all_healthy=true
        else
            sleep 5
        fi
    done
    
    echo ""
    if [ "$all_healthy" = true ]; then
        echo -e "${GREEN}========================================${NC}"
        echo -e "${GREEN}  All services are healthy!${NC}"
        echo -e "${GREEN}========================================${NC}"
    else
        echo -e "${YELLOW}========================================${NC}"
        echo -e "${YELLOW}  Some services may not be ready yet${NC}"
        echo -e "${YELLOW}========================================${NC}"
    fi
    
    # Show URLs
    echo ""
    echo -e "${CYAN}Service URLs:${NC}"
    echo -e "  - API Gateway:    http://localhost:8080"
    echo -e "  - Eureka:         http://localhost:8761"
    echo -e "  - Config Server:  http://localhost:8888"
    echo -e "  - Zipkin:         http://localhost:9411"
    echo -e "  - Prometheus:     http://localhost:9090"
    echo -e "  - Grafana:        http://localhost:3000"
    echo -e "  - Swagger UI:     http://localhost:8080/webjars/swagger-ui/index.html"
    echo ""
    
    # Show useful commands
    echo -e "${CYAN}Useful commands:${NC}"
    echo -e "${GRAY}  - View logs:      docker-compose logs -f${NC}"
    echo -e "${GRAY}  - Stop services:  docker-compose down${NC}"
    echo -e "${GRAY}  - Check health:   docker-compose ps${NC}"
    echo ""
fi
