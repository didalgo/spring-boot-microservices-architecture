# ========================================
# STAGE 1: Build
# ========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy parent POM
COPY pom.xml .

# Copy all module POMs (required for Maven reactor)
COPY common/pom.xml common/
COPY contracts/pom.xml contracts/
COPY config-server/pom.xml config-server/
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY order-service/pom.xml order-service/
COPY payment-service/pom.xml payment-service/
COPY notification-service/pom.xml notification-service/
COPY auth-service/pom.xml auth-service/

# Download dependencies (cached if POMs don't change)
RUN mvn dependency:go-offline -pl config-server -am

# Copy source code
COPY contracts contracts/
COPY common common/
COPY config-server config-server/

# Build
RUN mvn clean package -pl config-server -am -DskipTests

# ========================================
# STAGE 2: Runtime
# ========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy JAR from builder stage
COPY --from=builder /build/config-server/target/*.jar app.jar

# Create non-root user
RUN addgroup -S appuser && adduser -S appuser -G appuser && \
    chown -R appuser:appuser /app

USER appuser

EXPOSE 8888

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8888/actuator/health || exit 1

ENTRYPOINT ["java"]
CMD ["-jar", "app.jar"]