# Use an official Gradle image with JDK
FROM gradle:8.7.0-jdk17 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Build the project using Gradle wrapper
RUN ./gradlew clean build --no-daemon

# ================================
# Runtime image (Slim)
# ================================

FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port (change if needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
