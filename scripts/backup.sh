#!/bin/bash

# ========================================
# Backup Script
# ========================================

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;37m'
NC='\033[0m' # No Color

# Default backup directory
BACKUP_DIR="./backups/$(date +'%Y-%m-%d_%H-%M-%S')"

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --dir|-d)
            BACKUP_DIR="$2"
            shift 2
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --dir, -d <path>    Backup directory (default: ./backups/TIMESTAMP)"
            echo "  --help, -h          Show this help message"
            echo ""
            echo "Example:"
            echo "  $0                          # Backup with timestamp"
            echo "  $0 -d /opt/backups/manual   # Backup to specific directory"
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
echo -e "${CYAN}  Backup Script${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

# Create backup directory
mkdir -p "$BACKUP_DIR"
echo -e "${GRAY}Backup directory: $BACKUP_DIR${NC}"
echo ""

# Backup PostgreSQL
echo -e "${YELLOW}Backing up PostgreSQL...${NC}"
docker exec postgres pg_dump -U admin authdb > "$BACKUP_DIR/authdb.sql" 2>/dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ PostgreSQL backup complete${NC}"
else
    echo -e "${RED}✗ PostgreSQL backup failed${NC}"
fi

# Backup Volumes
echo -e "\n${YELLOW}Backing up volumes...${NC}"

# Kafka data
docker run --rm -v kafka-data:/data -v "$(pwd)":/backup alpine \
    tar czf "/backup/$BACKUP_DIR/kafka-data.tar.gz" -C /data . 2>/dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Kafka data backup complete${NC}"
else
    echo -e "${RED}✗ Kafka data backup failed${NC}"
fi

# Prometheus data
docker run --rm -v prometheus-data:/data -v "$(pwd)":/backup alpine \
    tar czf "/backup/$BACKUP_DIR/prometheus-data.tar.gz" -C /data . 2>/dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Prometheus data backup complete${NC}"
else
    echo -e "${RED}✗ Prometheus data backup failed${NC}"
fi

# Grafana data
docker run --rm -v grafana-data:/data -v "$(pwd)":/backup alpine \
    tar czf "/backup/$BACKUP_DIR/grafana-data.tar.gz" -C /data . 2>/dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Grafana data backup complete${NC}"
else
    echo -e "${RED}✗ Grafana data backup failed${NC}"
fi

# Backup configuration files
echo -e "\n${YELLOW}Backing up configuration...${NC}"

if [ -f ".env" ]; then
    cp .env "$BACKUP_DIR/.env"
    echo -e "${GREEN}✓ .env backup complete${NC}"
fi

if [ -f "docker-compose.yml" ]; then
    cp docker-compose.yml "$BACKUP_DIR/docker-compose.yml"
    echo -e "${GREEN}✓ docker-compose.yml backup complete${NC}"
fi

# Create backup manifest
cat > "$BACKUP_DIR/manifest.txt" << EOF
Backup created: $(date)
Hostname: $(hostname)
Docker version: $(docker --version)
Docker Compose version: $(docker-compose --version)

Contents:
- authdb.sql (PostgreSQL database)
- kafka-data.tar.gz (Kafka volume)
- prometheus-data.tar.gz (Prometheus volume)
- grafana-data.tar.gz (Grafana volume)
- .env (environment variables)
- docker-compose.yml (compose configuration)
EOF

echo -e "${GREEN}✓ Manifest created${NC}"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Backup complete!${NC}"
echo -e "${GREEN}  Location: $BACKUP_DIR${NC}"
echo -e "${GREEN}========================================${NC}"

# Show backup size
BACKUP_SIZE=$(du -sh "$BACKUP_DIR" | cut -f1)
echo -e "${GRAY}Backup size: $BACKUP_SIZE${NC}"
