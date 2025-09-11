# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Git 설치
RUN apt-get update && apt-get install -y git && rm -rf /var/lib/apt/lists/*

# Copy gradle files
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY gradlew ./
COPY .git .git

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (this layer will be cached if dependencies don't change)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build application
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create non-root user
RUN groupadd -g 1001 spring && \
    useradd -u 1001 -g spring -s /bin/bash spring

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to spring user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]