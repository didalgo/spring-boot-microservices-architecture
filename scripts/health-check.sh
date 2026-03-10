#!/bin/bash

# ========================================
# Microservices Health Check Script
# ========================================

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}  Health Check Report${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# Define services
declare -A services=(
    ["Config Server"]="8888:/actuator/health"
    ["Eureka Server"]="8761:/actuator/health"
    ["API Gateway"]="8080:/actuator/health"
    ["Auth Service"]="8084:/actuator/health"
    ["Order Service"]="8081:/actuator/health"
    ["Payment Service"]="8082:/actuator/health"
    ["Notification Service"]="8083:/actuator/health"
    ["Zipkin"]="9411:/health"
)

healthy_count=0
total_count=${#services[@]}

# Check each service
for service in "${!services[@]}"; do
    IFS=':' read -r port path <<< "${services[$service]}"
    url="http://localhost:${port}${path}"
    
    # Make request
    response=$(curl -s -w "\n%{http_code}" "$url" 2>/dev/null)
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n-1)
    
    # Check if healthy
    if [ "$http_code" = "200" ]; then
        if echo "$body" | grep -q '"status":"UP"' || echo "$body" | grep -q '"status":"up"'; then
            printf "%-30s ${GREEN}[HEALTHY]${NC}\n" "✓ $service"
            healthy_count=$((healthy_count + 1))
        else
            printf "%-30s ${YELLOW}[DEGRADED]${NC}\n" "✗ $service"
        fi
    else
        printf "%-30s ${RED}[DOWN]${NC}\n" "✗ $service"
    fi
done

echo ""
echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}  Summary: $healthy_count/$total_count services healthy${NC}"
echo -e "${CYAN}========================================${NC}"

if [ $healthy_count -eq $total_count ]; then
    echo -e "${GREEN}All systems operational!${NC}"
    exit 0
else
    echo -e "${YELLOW}Some services are not healthy${NC}"
    exit 1
fi
