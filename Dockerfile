# Multi-stage Dockerfile for Teamo (Task 06) Render Single Web Service Deployment

# Stage 1: Build React Frontend
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend

# Install dependencies
COPY task-06-teamo/frontend/package*.json ./
RUN npm ci || npm install

# Build React production bundle
COPY task-06-teamo/frontend ./
RUN npm run build

# Stage 2: Build Spring Boot Backend with Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app/backend

# Copy backend pom.xml
COPY task-06-teamo/backend/pom.xml ./

# Copy compiled React frontend static assets from Stage 1 into Spring Boot static resources
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static/

# Copy backend source code
COPY task-06-teamo/backend/src ./src

# Build production Spring Boot JAR (skipping unit tests during container image packaging)
RUN mvn clean package -DskipTests

# Stage 3: Lightweight Production Runtime Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy executable Spring Boot JAR from Stage 2
COPY --from=backend-builder /app/backend/target/*.jar app.jar

# Render exposes the PORT environment variable
ENV PORT=8080
EXPOSE ${PORT}

# Run the unified Spring Boot application
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
