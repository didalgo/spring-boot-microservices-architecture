# Microservices Scripts

This directory contains automation scripts to manage the Spring Boot microservices architecture.
All scripts are written in **Bash** and are compatible with Linux and macOS (and Windows via WSL2).

## Requirements

- Bash 4.0 or higher
- Docker and Docker Compose installed and running
- A `.env` file at the project root with the required environment variables

## Setup

Grant execution permissions to all scripts (first time only):

```bash
chmod +x scripts/*.sh
```

---

## Available Scripts

### 1. `startup.sh` — Start Services

Starts all microservices with health verification.

```bash
# Normal startup (background)
./scripts/startup.sh

# Build images before starting
./scripts/startup.sh --build

# Clean volumes + build + start (fresh state)
./scripts/startup.sh --clean --build

# Start and stream logs to console (foreground)
./scripts/startup.sh --verbose

# Show help
./scripts/startup.sh --help
```

| Option | Short | Description |
|--------|-------|-------------|
| `--build` | `-b` | Build Docker images before starting |
| `--clean` | `-c` | Remove existing containers and volumes first |
| `--verbose` | `-v` | Run in foreground and stream logs |

After startup, the following URLs will be available:

| Service | URL |
|---------|-----|
| API Gateway | http://localhost:8080 |
| Eureka Server | http://localhost:8761 |
| Config Server | http://localhost:8888 |
| Zipkin | http://localhost:9411 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |
| Swagger UI | http://localhost:8080/webjars/swagger-ui/index.html |

---

### 2. `shutdown.sh` — Stop Services

Stops all services gracefully.

```bash
# Normal shutdown (preserves volumes and data)
./scripts/shutdown.sh

# Shutdown and remove volumes (data will be lost)
./scripts/shutdown.sh --volumes

# Shutdown and remove images
./scripts/shutdown.sh --images

# Show help
./scripts/shutdown.sh --help
```

| Option | Short | Description |
|--------|-------|-------------|
| `--volumes` | `-v` | Remove Docker volumes (all data will be lost) |
| `--images` | `-i` | Remove Docker images |

> ⚠️ **Warning:** Using `--volumes` will permanently delete all persisted data including PostgreSQL, Kafka, Zookeeper, Prometheus, and Grafana volumes.

---

### 3. `health-check.sh` — Check Service Health

Checks the health status of all services by querying their `/actuator/health` endpoints.

```bash
./scripts/health-check.sh
```

Expected output:

```
✓ Config Server          [HEALTHY]
✓ Eureka Server          [HEALTHY]
✓ API Gateway            [HEALTHY]
✓ Auth Service           [HEALTHY]
✓ Order Service          [HEALTHY]
✓ Payment Service        [HEALTHY]
✓ Notification Service   [HEALTHY]
✓ Zipkin                 [HEALTHY]

Summary: 8/8 services healthy
All systems operational!
```

Exit codes: `0` if all services are healthy, `1` if any service is down or degraded.

---

### 4. `view-logs.sh` — View Service Logs

Displays logs for a specific service.

```bash
# List available services
./scripts/view-logs.sh

# View last 100 lines for a service
./scripts/view-logs.sh --service order-service

# Follow logs in real time
./scripts/view-logs.sh --service order-service --follow

# View last 500 lines and follow
./scripts/view-logs.sh -s api-gateway -t 500 -f

# Show help
./scripts/view-logs.sh --help
```

| Option | Short | Description |
|--------|-------|-------------|
| `--service <name>` | `-s` | Service name to view logs for |
| `--tail <number>` | `-t` | Number of lines to show (default: 100) |
| `--follow` | `-f` | Stream logs in real time |

---

### 5. `e2e-test.sh` — End-to-End Tests

Runs automated end-to-end tests against the full running stack.

```bash
./scripts/e2e-test.sh
```

Tests executed in order:

1. **Health checks** — Config Server, Eureka, API Gateway, Auth Service
2. **Authentication** — Login with admin credentials, JWT token extraction
3. **Order management** — Create order, get order by ID, list all orders
4. **Payment processing** — Process payment, get payment by ID
5. **Security** — Verify that unauthenticated requests return `401`

Exit codes: `0` if all tests pass, `1` if any test fails.

> The stack must be fully up and healthy before running this script. Use `health-check.sh` to verify.

---

### 6. `backup.sh` — Backup Data

Creates a full backup of the system including the database, Docker volumes, and configuration files.

```bash
# Backup with automatic timestamp directory
./scripts/backup.sh

# Backup to a specific directory
./scripts/backup.sh --dir /opt/backups/my-backup

# Show help
./scripts/backup.sh --help
```

| Option | Short | Description |
|--------|-------|-------------|
| `--dir <path>` | `-d` | Target backup directory (default: `./backups/TIMESTAMP`) |

Backup contents:

| File | Description |
|------|-------------|
| `authdb.sql` | PostgreSQL database dump |
| `kafka-data.tar.gz` | Kafka volume snapshot |
| `prometheus-data.tar.gz` | Prometheus volume snapshot |
| `grafana-data.tar.gz` | Grafana volume snapshot |
| `.env` | Environment variables |
| `docker-compose.yml` | Compose configuration |
| `manifest.txt` | Backup metadata (date, host, versions) |

---

### 7. `restore.sh` — Restore from Backup

Restores the system from a previously created backup.

```bash
./scripts/restore.sh ./backups/2024-03-07_10-30-00
```

The script will:
1. Display the backup manifest
2. Ask for confirmation before overwriting any data
3. Stop all running services
4. Restore PostgreSQL, Kafka, Prometheus, and Grafana volumes
5. Restore `.env` and `docker-compose.yml`

> ⚠️ **Warning:** This will replace all current data. A confirmation prompt will appear before any destructive action is taken.

After restore, start services with:

```bash
docker-compose up -d
```

---

## Typical Workflows

### Daily Development

```bash
# Start the system
./scripts/startup.sh --build

# Verify all services are up
./scripts/health-check.sh

# Watch logs while developing
./scripts/view-logs.sh -s order-service -f

# Shut down at the end of the day
./scripts/shutdown.sh
```

### Running Tests

```bash
# Start from a clean state
./scripts/startup.sh --clean --build

# Wait for all services to be healthy
./scripts/health-check.sh

# Run end-to-end tests
./scripts/e2e-test.sh

# Inspect logs if tests fail
./scripts/view-logs.sh -s order-service -t 500
```

### Maintenance

```bash
# Backup before applying changes
./scripts/backup.sh

# Apply changes...

# If something goes wrong, restore
./scripts/restore.sh ./backups/2024-03-07_10-30-00
```

### Full Reset (clean slate)

```bash
# Stop everything and remove all volumes
./scripts/shutdown.sh --volumes

# Rebuild and start fresh
./scripts/startup.sh --build
```

---

## Troubleshooting

**Script not executable:**
```bash
chmod +x scripts/*.sh
```

**Docker not running:**
```bash
# Check Docker status
docker info
```

**Services not healthy after startup:**
```bash
# Check individual service logs
./scripts/view-logs.sh -s config-server -t 200
./scripts/view-logs.sh -s eureka-server -t 200
```

**Kafka InconsistentClusterIdException after restart:**
```bash
# This happens when Kafka and Zookeeper volumes get out of sync
./scripts/shutdown.sh --volumes
./scripts/startup.sh --build
```

**Missing `.env` file:**  
Copy `.env.example` from the project root and fill in the required values before running any script.

---

## Related Links

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Cloud Config](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)
- [Project README](../README.md)
