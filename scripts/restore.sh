#!/bin/bash

# ========================================
# Restore Script
# ========================================

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;37m'
NC='\033[0m' # No Color

# Check if backup directory is provided
if [ $# -eq 0 ]; then
    echo -e "${RED}ERROR: Backup directory not specified${NC}"
    echo "Usage: $0 <backup-directory>"
    echo ""
    echo "Example:"
    echo "  $0 ./backups/2024-03-07_10-30-00"
    exit 1
fi

BACKUP_DIR="$1"

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}  Restore Script${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# Verify backup directory exists
if [ ! -d "$BACKUP_DIR" ]; then
    echo -e "${RED}ERROR: Backup directory not found: $BACKUP_DIR${NC}"
    exit 1
fi

echo -e "${GRAY}Restoring from: $BACKUP_DIR${NC}"
echo ""

# Show manifest if exists
if [ -f "$BACKUP_DIR/manifest.txt" ]; then
    echo -e "${CYAN}Backup Manifest:${NC}"
    cat "$BACKUP_DIR/manifest.txt"
    echo ""
fi

# Confirm restore
echo -e "${YELLOW}WARNING: This will replace current data!${NC}"
read -p "Do you want to continue? (yes/no): " -r
echo ""

if [[ ! $REPLY =~ ^[Yy]es$ ]]; then
    echo -e "${YELLOW}Restore cancelled${NC}"
    exit 0
fi

# Stop services
echo -e "${YELLOW}Stopping services...${NC}"
docker-compose down
echo -e "${GREEN}✓ Services stopped${NC}"
echo ""

# Restore PostgreSQL
if [ -f "$BACKUP_DIR/authdb.sql" ]; then
    echo -e "${YELLOW}Restoring PostgreSQL...${NC}"
    
    # Start PostgreSQL
    docker-compose up -d postgres
    echo -e "${GRAY}Waiting for PostgreSQL to be ready...${NC}"
    sleep 10
    
    # Restore database
    docker exec -i postgres psql -U admin authdb < "$BACKUP_DIR/authdb.sql" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ PostgreSQL restore complete${NC}"
    else
        echo -e "${RED}✗ PostgreSQL restore failed${NC}"
    fi
    
    # Stop PostgreSQL
    docker-compose stop postgres
else
    echo -e "${YELLOW}⚠ PostgreSQL backup not found, skipping...${NC}"
fi

# Restore Kafka data
if [ -f "$BACKUP_DIR/kafka-data.tar.gz" ]; then
    echo -e "\n${YELLOW}Restoring Kafka data...${NC}"
    
    docker run --rm -v kafka-data:/data -v "$(pwd)":/backup alpine \
        sh -c "rm -rf /data/* && tar xzf /backup/$BACKUP_DIR/kafka-data.tar.gz -C /data" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Kafka data restore complete${NC}"
    else
        echo -e "${RED}✗ Kafka data restore failed${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Kafka backup not found, skipping...${NC}"
fi

# Restore Prometheus data
if [ -f "$BACKUP_DIR/prometheus-data.tar.gz" ]; then
    echo -e "\n${YELLOW}Restoring Prometheus data...${NC}"
    
    docker run --rm -v prometheus-data:/data -v "$(pwd)":/backup alpine \
        sh -c "rm -rf /data/* && tar xzf /backup/$BACKUP_DIR/prometheus-data.tar.gz -C /data" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Prometheus data restore complete${NC}"
    else
        echo -e "${RED}✗ Prometheus data restore failed${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Prometheus backup not found, skipping...${NC}"
fi

# Restore Grafana data
if [ -f "$BACKUP_DIR/grafana-data.tar.gz" ]; then
    echo -e "\n${YELLOW}Restoring Grafana data...${NC}"
    
    docker run --rm -v grafana-data:/data -v "$(pwd)":/backup alpine \
        sh -c "rm -rf /data/* && tar xzf /backup/$BACKUP_DIR/grafana-data.tar.gz -C /data" 2>/dev/null
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Grafana data restore complete${NC}"
    else
        echo -e "${RED}✗ Grafana data restore failed${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Grafana backup not found, skipping...${NC}"
fi

# Restore configuration
echo -e "\n${YELLOW}Restoring configuration...${NC}"

if [ -f "$BACKUP_DIR/.env" ]; then
    cp "$BACKUP_DIR/.env" .env
    echo -e "${GREEN}✓ .env restore complete${NC}"
fi

if [ -f "$BACKUP_DIR/docker-compose.yml" ]; then
    cp "$BACKUP_DIR/docker-compose.yml" docker-compose.yml
    echo -e "${GREEN}✓ docker-compose.yml restore complete${NC}"
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Restore complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${YELLOW}Start services with: ${NC}${GRAY}docker-compose up -d${NC}"
