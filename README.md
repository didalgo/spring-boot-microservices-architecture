# Spring Boot Microservices Architecture

A production-grade microservices system built with Spring Boot 3.x, demonstrating real-world patterns for distributed systems, event-driven architecture, and cloud-native deployment.

---

## Overview

This project implements a multi-service e-commerce backend covering authentication, order management, payment processing, and notifications. It was designed as a hands-on exploration of the patterns and tools used in enterprise Java systems — not a toy application, but a realistic architecture with proper layering, security, observability, and deployment automation.

---

## Architecture

```
                         ┌─────────────────────────────────────────────┐
                         │              Kubernetes Cluster               │
                         │                                              │
   External Traffic      │  ┌─────────────┐                            │
   ────────────────────► │  │ API Gateway  │  :8080                     │
                         │  └──────┬───────┘                            │
                         │         │  JWT validation + routing           │
                         │  ┌──────▼──────────────────────────────┐    │
                         │  │           Platform Layer             │    │
                         │  │   Config Server     Eureka Server    │    │
                         │  └──────────────────────────────────────┘    │
                         │                                              │
                         │  ┌──────────────────────────────────────┐    │
                         │  │           Services Layer             │    │
                         │  │  Auth   Order   Payment  Notification│    │
                         │  └──────────────────────────────────────┘    │
                         │                                              │
                         │  ┌──────────────────────────────────────┐    │
                         │  │        Infrastructure Layer          │    │
                         │  │  PostgreSQL   Kafka   Zipkin         │    │
                         │  └──────────────────────────────────────┘    │
                         └─────────────────────────────────────────────┘
```

### Services

| Service | Port | Responsibility |
|---|---|---|
| `api-gateway` | 8080 | Request routing, JWT validation, circuit breaking |
| `auth-service` | 8084 | User registration, login, JWT issuance |
| `order-service` | 8081 | Order lifecycle management |
| `payment-service` | 8082 | Payment processing, Kafka consumer/producer |
| `notification-service` | 8083 | Event-driven notifications via Kafka |
| `config-server` | 8888 | Centralized configuration (Spring Cloud Config) |
| `eureka-server` | 8761 | Service discovery (Spring Cloud Netflix Eureka) |

---

## Tech Stack

### Backend
- **Java 21** — Records, sealed classes, pattern matching
- **Spring Boot 3.2** — Web, Security, Data JPA, Actuator
- **Spring Cloud 2023** — Gateway, Config, Eureka, OpenFeign, Circuit Breaker

### Messaging
- **Apache Kafka** — Async event streaming between order and payment services
- **Zookeeper** — Kafka cluster coordination

### Data
- **PostgreSQL 15** — Persistent storage for auth and order services
- **Spring Data JPA / Hibernate** — ORM layer

### Security
- **JWT (JJWT)** — Stateless authentication
- **Spring Security** — Filter chain, method-level authorization

### Observability
- **Zipkin** — Distributed tracing
- **Micrometer + Prometheus** — Metrics collection
- **Spring Boot Actuator** — Health, info, metrics endpoints

### Infrastructure
- **Docker + Docker Compose** — Local development stack
- **Kubernetes (minikube)** — Container orchestration
- **Helm-ready manifests** — Namespace, ConfigMaps, Secrets, Deployments, Services, StatefulSets, PVCs

### API Documentation
- **SpringDoc OpenAPI 3** — Swagger UI aggregated at the API Gateway

### Build & CI
- **Maven multi-module** — Shared `contracts` and `common` modules
- **GitHub Actions** — Build, test, Docker image push pipeline
- **JaCoCo** — Code coverage reporting

---

## Key Patterns Implemented

**API Gateway Pattern** — Single entry point with JWT validation, rate limiting via Resilience4j, and route-based circuit breakers with fallbacks.

**Event-Driven Architecture** — Order creation triggers a Kafka event consumed by the payment service. Payment result triggers a notification event. Services are fully decoupled.

**Externalized Configuration** — All environment-specific config lives in a dedicated Git repository served by Spring Cloud Config Server. Services load config by profile (`docker`, `kubernetes`) at startup.

**Service Discovery** — Eureka registry with health-aware load balancing. The API Gateway resolves service instances via `lb://service-name` URIs.

**Distributed Tracing** — Micrometer Brave propagates trace/span IDs across service boundaries. Traces are visualized in Zipkin.

**Circuit Breaker** — Resilience4j circuit breakers on all business routes in the gateway, with configurable thresholds and automatic half-open recovery.

---

## Running Locally

### Prerequisites
- Docker and Docker Compose
- Java 21
- Maven 3.9+

### Docker Compose (recommended)

```bash
# Clone the config repository alongside this one
git clone https://github.com/didalgo/microservices-config ../microservices-config

# Copy and configure environment
cp .env.example .env

# Build all images
docker-compose build

# Start the full stack
./scripts/startup.sh --build

# Verify all services are healthy
./scripts/health-check.sh
```

Services will be available at:
- API Gateway: http://localhost:8080
- Swagger UI: http://localhost:8080/webjars/swagger-ui/index.html
- Eureka Dashboard: http://localhost:8761
- Zipkin: http://localhost:9411

### Kubernetes (minikube)

```bash
# Start minikube
minikube start --memory=4096 --cpus=4

# Mount config repository
minikube mount /path/to/microservices-config:/config &

# Build images into minikube's Docker daemon
eval $(minikube docker-env)
docker-compose build
eval $(minikube docker-env --unset)

# Apply manifests in order
kubectl apply -f k8s/namespace.yml
kubectl apply -f k8s/configmap.yml
kubectl apply -f k8s/secrets.yml
kubectl apply -f k8s/infrastructure/
kubectl apply -f k8s/platform/
kubectl apply -f k8s/services/

# Access the API Gateway
kubectl port-forward service/api-gateway 8080:8080 -n microservices
```

---

## Project Structure

```
spring-boot-microservices-architecture/
├── contracts/                  # Shared DTOs and Kafka event contracts
├── common/                     # Shared utilities and base classes
├── api-gateway/                # Spring Cloud Gateway
├── auth-service/               # JWT authentication
├── order-service/              # Order management
├── payment-service/            # Payment processing
├── notification-service/       # Event-driven notifications
├── config-server/              # Spring Cloud Config Server
├── eureka-server/              # Service discovery
├── k8s/                        # Kubernetes manifests
│   ├── infrastructure/         # Postgres, Kafka, Zookeeper, Zipkin
│   ├── platform/               # Config Server, Eureka
│   └── services/               # Business microservices
├── scripts/                    # Bash automation scripts
│   ├── startup.sh
│   ├── shutdown.sh
│   ├── health-check.sh
│   ├── view-logs.sh
│   ├── e2e-test.sh
│   ├── backup.sh
│   └── restore.sh
├── docker-compose.yml
└── .github/
    └── workflows/
        └── build-and-deploy.yml
```

---

## API Reference

Full interactive documentation available at `http://localhost:8080/webjars/swagger-ui/index.html` when the stack is running.

### Authentication

```bash
# Register
POST /auth/register
{ "username": "john", "email": "john@example.com", "password": "secret123" }

# Login
POST /auth/login
{ "username": "john", "password": "secret123" }
# Returns: { "accessToken": "eyJ...", "refreshToken": "...", "expiresIn": 86400 }
```

### Orders (requires Bearer token)

```bash
# Create order
POST /api/orders
Authorization: Bearer <token>
{ "customerId": "CUST-001", "productId": "PROD-001", "quantity": 2, "totalAmount": 199.99 }

# Get order
GET /api/orders/{orderId}
Authorization: Bearer <token>
```

---

## Automation Scripts

```bash
# Start the stack (with optional build and clean flags)
./scripts/startup.sh [--build] [--clean] [--verbose]

# Stop the stack (with optional volume removal)
./scripts/shutdown.sh [--volumes] [--images]

# Check health of all services
./scripts/health-check.sh

# View logs for a specific service
./scripts/view-logs.sh --service order-service [--follow] [--tail 200]

# Run end-to-end tests
./scripts/e2e-test.sh

# Backup data volumes and config
./scripts/backup.sh [--dir /path/to/backup]

# Restore from backup
./scripts/restore.sh ./backups/2024-03-07_10-30-00
```

---

## Environment Variables

Copy `.env.example` to `.env` and configure:

| Variable | Description |
|---|---|
| `POSTGRES_DB` | Database name |
| `POSTGRES_USER` | Database user |
| `POSTGRES_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key for JWT signing (min 32 chars) |
| `JWT_EXPIRATION` | Access token TTL in seconds |
| `JWT_REFRESH_EXPIRATION` | Refresh token TTL in seconds |
| `CONFIG_REPO_PATH` | Absolute path to the config repository |
| `VERSION` | Image version tag |

---

## License

MIT
