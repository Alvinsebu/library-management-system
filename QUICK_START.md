# Quick Start Commands

## 📦 Setup & Build

### 1. Install Prerequisites

```bash
# Install Java 21
# macOS
brew install openjdk@21

# Ubuntu
sudo apt-get install openjdk-21-jdk

# Windows - Download from eclipse-temurin.net
```

### 2. Install MongoDB

```bash
# macOS
brew install mongodb-community
brew services start mongodb-community

# Ubuntu
sudo apt-get install -y mongodb-org
sudo systemctl start mongod

# Windows - Download from mongodb.com/download
```

### 3. Navigate to Project

```bash
cd c:\kotlin\library-management-system
```

## 🔨 Build Commands

```bash
# Build project
./gradlew build

# Build without tests
./gradlew build -x test

# Clean build
./gradlew clean build

# Generate JAR
./gradlew bootJar

# Run tests
./gradlew test

# View dependencies
./gradlew dependencies
```

## 🚀 Run Locally

### Option 1: Using Gradle

```bash
# Start application
./gradlew bootRun

# Application runs on http://localhost:8080
```

### Option 2: Using JAR

```bash
# Build JAR
./gradlew bootJar

# Run JAR
java -jar build/libs/library-management-system-1.0.0.jar

# Application runs on http://localhost:8080
```

### Option 3: Using IDE

**IntelliJ IDEA:**
- Right-click `LibraryManagementSystemApplication.kt`
- Select "Run LibraryManagementSystemApplicationKt"

**VS Code:**
- Press `F5` to debug
- Or use Command Palette → "Debug: Java"

## 🐳 Docker Commands

### Start Full Stack

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Remove volumes (WARNING: deletes data)
docker-compose down -v
```

### Individual Services

```bash
# Start only app
docker-compose up -d app

# Start only MongoDB
docker-compose up -d mongodb

# Start only Prometheus
docker-compose up -d prometheus

# Start only Grafana
docker-compose up -d grafana
```

### View Service Status

```bash
# List running containers
docker-compose ps

# View specific service logs
docker-compose logs mongodb
docker-compose logs app
docker-compose logs prometheus
docker-compose logs grafana
```

## 🌐 Access Services

### With Local Setup

```bash
# Application API
http://localhost:8080/api

# Swagger UI
http://localhost:8080/swagger-ui.html

# Health Check
http://localhost:8080/actuator/health

# Prometheus
http://localhost:9000

# Grafana
http://localhost:3000 (admin/admin123)

# MongoDB
mongodb://localhost:27017
```

### With Docker Setup

```bash
# Application API
http://localhost:8080/api

# Swagger UI
http://localhost:8080/swagger-ui.html

# Health Check
http://localhost:8080/actuator/health

# Prometheus
http://localhost:9000

# Grafana
http://localhost:3000 (admin/admin123)

# MongoDB
mongodb://admin:password123@localhost:27017/library_db
```

## 📝 Test API Endpoints

### 1. Signup

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "Password123"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "Password123"
  }'
```

**Save the token from response for next requests**

### 3. Create Book (Admin)

```bash
curl -X POST http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "policy": "NORMAL"
  }'
```

### 4. View Available Books

```bash
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 5. Borrow Book

```bash
curl -X POST http://localhost:8080/api/books/{BOOK_ID}/borrow \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "expiryMinutes": 1440
  }'
```

### 6. Return Book

```bash
curl -X POST http://localhost:8080/api/books/{BOOK_ID}/return \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 🧪 Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test

```bash
./gradlew test --tests AuthServiceTest
./gradlew test --tests BookServiceTest
```

### Generate Coverage Report

```bash
./gradlew test jacocoTestReport
# Open: build/reports/jacoco/test/html/index.html
```

## 📊 Monitoring & Observability

### Prometheus Metrics

```bash
# View metrics
curl http://localhost:9090/actuator/prometheus

# Query metrics
curl 'http://localhost:9000/api/v1/query?query=http_request_duration_seconds'
```

### Health Checks

```bash
# Full health
curl http://localhost:8080/actuator/health

# Database health
curl http://localhost:8080/actuator/health/db

# Readiness
curl http://localhost:8080/actuator/health/readiness
```

### Grafana Setup

1. Open http://localhost:3000
2. Login: admin / admin123
3. Add Prometheus datasource: http://prometheus:9090
4. Create dashboards

## 🗄️ Database Commands

### MongoDB Connection

```bash
# Connect to local MongoDB
mongosh

# Connect to Docker MongoDB
mongosh mongodb://admin:password123@localhost:27017 --authenticationDatabase=admin
```

### Database Operations

```bash
# Use library database
use library_db

# Show collections
show collections

# View users
db.users.find()

# View books
db.books.find()

# Count documents
db.books.countDocuments()

# Backup
mongodump --out=./backup

# Restore
mongorestore ./backup
```

## 🛠️ Development Commands

### Format Code

```bash
# Format Kotlin code
./gradlew ktlintFormat

# Or in IDE
Ctrl+Alt+L (IntelliJ)
```

### Check Dependencies

```bash
./gradlew dependencyUpdates
./gradlew dependencyCheck
```

### View Gradle Tasks

```bash
./gradlew tasks
```

## 🔍 Troubleshooting

### Kill Port

```bash
# Find process on port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

### MongoDB Not Running

```bash
# Check status
ps aux | grep mongod

# Start MongoDB
mongod

# Or with Docker
docker run -d -p 27017:27017 mongo:7.0
```

### Clear Gradle Cache

```bash
./gradlew clean
rm -rf .gradle
```

### Reset Docker

```bash
# Remove all containers
docker-compose down -v

# Rebuild images
docker-compose build --no-cache

# Start fresh
docker-compose up -d
```

## 📚 Documentation

### Main Files

```bash
# Overview
cat README.md

# API Examples
cat API_EXAMPLES.md

# Development Guide
cat DEVELOPMENT.md

# Deployment Guide
cat DEPLOYMENT.md

# Configuration Guide
cat CONFIG.md

# Project Summary
cat PROJECT_SUMMARY.md

# Deliverables
cat DELIVERABLES.md
```

## 🚀 Production Deployment

### Build Docker Image

```bash
# Build
docker build -t library-management-system:1.0.0 .

# Tag for registry
docker tag library-management-system:1.0.0 \
  registry.example.com/library-management-system:1.0.0

# Push
docker push registry.example.com/library-management-system:1.0.0
```

### Deploy with Docker Compose

```bash
# Set environment
export MONGO_PASSWORD=your-password
export JWT_SECRET=your-secret
export GRAFANA_PASSWORD=admin

# Deploy
docker-compose -f docker-compose.prod.yml up -d

# Check status
docker-compose -f docker-compose.prod.yml ps

# View logs
docker-compose -f docker-compose.prod.yml logs -f app
```

## 📋 Environment Variables

### Development

```bash
export SPRING_DATA_MONGODB_URI=mongodb://localhost:27017
export JWT_SECRET=dev-secret-key
export SPRING_PROFILES_ACTIVE=dev
```

### Production

```bash
export SPRING_DATA_MONGODB_URI=mongodb://admin:password@prod-mongo:27017
export JWT_SECRET=your-production-secret-key
export SPRING_PROFILES_ACTIVE=prod
export JAVA_OPTS="-Xmx1g -Xms1g"
```

## 🔐 Security Checks

```bash
# Dependency vulnerability scan
./gradlew dependencyCheckAnalyze

# View security scan report
open build/reports/dependency-check-report.html
```

## 📞 Useful Links

- **Kotlin Docs**: https://kotlinlang.org/docs/
- **Spring Boot**: https://spring.io/projects/spring-boot
- **MongoDB**: https://docs.mongodb.com/
- **gRPC**: https://grpc.io/docs/
- **Docker**: https://docs.docker.com/
- **JWT**: https://tools.ietf.org/html/rfc7519

## ✅ Quick Verification

```bash
# 1. Build successful
./gradlew build

# 2. Tests passing
./gradlew test

# 3. Application starts
./gradlew bootRun &

# 4. API responds
curl http://localhost:8080/actuator/health

# 5. Swagger accessible
open http://localhost:8080/swagger-ui.html
```

## 🎯 Summary

**Local Development**:
```bash
./gradlew bootRun
# Access: http://localhost:8080/swagger-ui.html
```

**Docker Deployment**:
```bash
docker-compose up -d
# Access: http://localhost:8080/swagger-ui.html
```

**Production JAR**:
```bash
./gradlew bootJar
java -jar build/libs/library-management-system-1.0.0.jar
```

---

**Happy Developing! 🚀**
