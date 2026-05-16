# Library Management System

A complete, production-ready backend system for library management built with **Kotlin**, **Spring Boot**, **MongoDB**, **JWT Authentication**, **gRPC**, and **Docker**.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Build & Run](#build--run)
- [Docker Setup](#docker-setup)
- [API Documentation](#api-documentation)
- [gRPC Services](#grpc-services)
- [Observability](#observability)
- [Testing](#testing)
- [Database Schema](#database-schema)
- [Git Workflow](#git-workflow)
- [Troubleshooting](#troubleshooting)

## Features

### Authentication & Security
- ✅ User signup and login
- ✅ JWT-based authentication
- ✅ BCrypt password hashing
- ✅ Role-based access control (RBAC)
- ✅ Secure API endpoints

### Book Management
- ✅ Admin can add and view all books
- ✅ Users can borrow and return books
- ✅ Support for multiple book policies (NORMAL, EXPIRY, END_OF_DAY)
- ✅ Automatic book return for expired books
- ✅ Prevent duplicate borrowing

### Advanced Features
- ✅ Scheduled tasks for automatic returns
- ✅ REST APIs with comprehensive error handling
- ✅ gRPC services for high-performance operations
- ✅ Prometheus metrics & Grafana dashboards
- ✅ Docker & Docker Compose setup
- ✅ Swagger/OpenAPI documentation
- ✅ Comprehensive unit & integration tests

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin 1.9.20 |
| **Framework** | Spring Boot 3.2.0 |
| **Build Tool** | Gradle (Kotlin DSL) |
| **Database** | MongoDB 7.0 |
| **Authentication** | JWT (JJWT) |
| **Password Encoding** | BCrypt |
| **RPC** | gRPC + Protobuf |
| **Observability** | Prometheus + Grafana + Micrometer |
| **Documentation** | Swagger/OpenAPI 3.0 |
| **Containerization** | Docker & Docker Compose |
| **Testing** | JUnit 5, Kotest, MockK |

## Architecture

### Clean Architecture Layers

```
Controller (REST, gRPC)
    ↓
Service (Business Logic)
    ↓
Repository (Data Access)
    ↓
Model (Domain Objects)
```

### Directory Structure

```
library-management-system/
├── src/
│   ├── main/
│   │   ├── kotlin/com/libmgmt/
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── service/             # Business Logic
│   │   │   ├── repository/          # Data Access Layer
│   │   │   ├── model/               # Domain Models
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── config/              # Configuration Classes
│   │   │   ├── security/            # Security & JWT
│   │   │   ├── scheduler/           # Scheduled Tasks
│   │   │   ├── grpc/                # gRPC Services
│   │   │   ├── exception/           # Custom Exceptions
│   │   │   ├── util/                # Utility Classes
│   │   │   └── LibraryManagementSystemApplication.kt
│   │   ├── proto/                   # gRPC Protobuf Files
│   │   └── resources/
│   │       └── application.yml      # Configuration
│   └── test/
│       └── kotlin/com/libmgmt/      # Test Classes
├── docker/
│   └── init-mongo.js                # MongoDB Initialization
├── prometheus/
│   ├── prometheus.yml               # Prometheus Config
│   └── grafana-provisioning/        # Grafana Setup
├── build.gradle.kts                 # Gradle Build File
├── Dockerfile                       # Docker Image
├── docker-compose.yml               # Docker Compose
└── README.md                        # This File
```

## Prerequisites

- **Java 21+** (OpenJDK or Eclipse Temurin)
- **Gradle 8.0+** (or use `./gradlew`)
- **Docker & Docker Compose** (for containerized setup)
- **MongoDB 7.0+** (local or Docker)
- **Git** (for version control)

## Setup Instructions

### Local Development Setup

#### 1. Clone the Project

```bash
cd /path/to/kotlin
```

#### 2. Install Java 21

```bash
# Using SDKMAN (recommended)
sdk install java 21.0.0-tem
sdk use java 21.0.0-tem

# Or download from eclipse-temurin.net
```

#### 3. Install MongoDB (Local)

```bash
# macOS
brew tap mongodb/brew
brew install mongodb-community

# Ubuntu
sudo apt-get install -y mongodb-org

# Windows
# Download from https://www.mongodb.com/try/download/community
```

#### 4. Start MongoDB

```bash
# macOS/Linux
mongod

# Windows
"C:\Program Files\MongoDB\Server\7.0\bin\mongod.exe"
```

#### 5. Initialize MongoDB Database

```bash
# Connect to MongoDB
mongosh

# Run initialization
use library_db
db.createCollection('users')
db.createCollection('books')
db.users.createIndex({ email: 1 }, { unique: true })
db.books.createIndex({ available: 1 })
db.books.createIndex({ borrowedBy: 1 })
```

#### 6. Build the Project

```bash
cd library-management-system

# Build project
./gradlew build

# Or using Gradle directly
gradle build
```

#### 7. Configure Application

Edit `src/main/resources/application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017
      database: library_db

jwt:
  secret: "your-super-secret-key-change-in-production"
  expiration: 86400000
```

#### 8. Run the Application

```bash
./gradlew bootRun

# Application starts on http://localhost:8080
```

## Build & Run

### Gradle Commands

```bash
# Build project
./gradlew build

# Run application
./gradlew bootRun

# Run tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Generate JAR
./gradlew bootJar

# Clean build
./gradlew clean build

# View dependency tree
./gradlew dependencies
```

### Running the JAR

```bash
# Build JAR
./gradlew bootJar

# Run JAR
java -jar build/libs/library-management-system-1.0.0.jar

# Run with custom properties
java -Dspring.data.mongodb.uri=mongodb://host:27017 \
     -Djwt.secret=your-secret \
     -jar build/libs/library-management-system-1.0.0.jar
```

## Docker Setup

### Build and Run with Docker Compose

```bash
cd library-management-system

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Remove volumes
docker-compose down -v
```

### Services Started

| Service | Port | URL | Credentials |
|---------|------|-----|-------------|
| **Application** | 8080 | http://localhost:8080 | N/A |
| **Actuator** | 9090 | http://localhost:9090/actuator | N/A |
| **gRPC** | 9091 | localhost:9091 | N/A |
| **MongoDB** | 27017 | mongodb://admin:password123@localhost:27017 | admin/password123 |
| **Prometheus** | 9000 | http://localhost:9000 | N/A |
| **Grafana** | 3000 | http://localhost:3000 | admin/admin123 |

### Build Docker Image Manually

```bash
# Build image
docker build -t library-management-system:1.0.0 .

# Run container
docker run -d \
  -p 8080:8080 \
  -p 9090:9090 \
  -p 9091:9091 \
  -e SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017 \
  --name library-app \
  library-management-system:1.0.0
```

## API Documentation

### Base URL
- **Development**: `http://localhost:8080/api`
- **Production**: `https://api.library.com/api`

### Swagger UI
- **Swagger**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Authentication

All protected endpoints require JWT token in Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ...
```

### Authentication Endpoints

#### 1. Signup

```
POST /auth/signup
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword123"
}

Response 201:
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiVXNlcklkIjoiNTA3ZjFmNzdiY2Y4NmNkNzk5NDM5MDExIiwiaWF0IjoxNjcwMDAwMDAwLCJleHAiOjE2NzAwODY0MDB9...",
    "message": "User registered successfully"
  }
}
```

#### 2. Login

```
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePassword123"
}

Response 200:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjcwMDAwMDAwLCJleHAiOjE2NzAwODY0MDB9..."
  }
}
```

### Admin Endpoints

#### 1. Create Book

```
POST /admin/books
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "policy": "NORMAL"
}

Response 201:
{
  "success": true,
  "message": "Book added successfully",
  "data": {
    "id": "507f1f77bcf86cd799439012",
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "available": true,
    "borrowedBy": null,
    "borrowedAt": null,
    "expiryAt": null,
    "policy": "NORMAL"
  }
}
```

#### 2. Get All Books

```
GET /admin/books
Authorization: Bearer <JWT_TOKEN>

Response 200:
{
  "success": true,
  "message": "Books retrieved successfully",
  "data": [
    {
      "id": "507f1f77bcf86cd799439012",
      "title": "The Great Gatsby",
      "author": "F. Scott Fitzgerald",
      "available": true,
      "borrowedBy": null,
      "borrowedAt": null,
      "expiryAt": null,
      "policy": "NORMAL"
    }
  ]
}
```

### User Endpoints

#### 1. Get Available Books

```
GET /books
Authorization: Bearer <JWT_TOKEN>

Response 200:
{
  "success": true,
  "message": "Available books retrieved",
  "data": [
    {
      "id": "507f1f77bcf86cd799439012",
      "title": "The Great Gatsby",
      "author": "F. Scott Fitzgerald",
      "available": true,
      "borrowedBy": null,
      "borrowedAt": null,
      "expiryAt": null,
      "policy": "NORMAL"
    }
  ]
}
```

#### 2. Get Book By ID

```
GET /books/{id}
Authorization: Bearer <JWT_TOKEN>

Response 200:
{
  "success": true,
  "message": "Book retrieved",
  "data": {
    "id": "507f1f77bcf86cd799439012",
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "available": true,
    "borrowedBy": null,
    "borrowedAt": null,
    "expiryAt": null,
    "policy": "NORMAL"
  }
}
```

#### 3. Borrow Book

```
POST /books/{id}/borrow
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "expiryMinutes": 1440
}

Response 200:
{
  "success": true,
  "message": "Book borrowed successfully",
  "data": {
    "id": "507f1f77bcf86cd799439012",
    "title": "The Great Gatsby",
    "borrowedBy": "507f1f77bcf86cd799439011",
    "borrowedAt": "2024-05-16T10:30:00",
    "expiryAt": "2024-05-17T10:30:00",
    "message": "Book borrowed successfully"
  }
}
```

#### 4. Return Book

```
POST /books/{id}/return
Authorization: Bearer <JWT_TOKEN>

Response 200:
{
  "success": true,
  "message": "Book returned successfully",
  "data": {
    "id": "507f1f77bcf86cd799439012",
    "title": "The Great Gatsby",
    "message": "Book returned successfully"
  }
}
```

#### 5. Get User's Borrowed Books

```
GET /books/user/borrowed
Authorization: Bearer <JWT_TOKEN>

Response 200:
{
  "success": true,
  "message": "Borrowed books retrieved",
  "data": {
    "userId": "507f1f77bcf86cd799439011",
    "books": [
      {
        "id": "507f1f77bcf86cd799439012",
        "title": "The Great Gatsby",
        "author": "F. Scott Fitzgerald",
        "available": false,
        "borrowedBy": "507f1f77bcf86cd799439011",
        "borrowedAt": "2024-05-16T10:30:00",
        "expiryAt": "2024-05-17T10:30:00",
        "policy": "EXPIRY"
      }
    ],
    "count": 1
  }
}
```

#### 6. Get User Profile

```
GET /users/me
Authorization: Bearer <JWT_TOKEN>

Response 200:
{
  "success": true,
  "message": "User profile retrieved",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "active": true,
    "createdAt": "2024-05-15T10:00:00",
    "updatedAt": "2024-05-15T10:00:00"
  }
}
```

### cURL Examples

```bash
# Signup
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "SecurePassword123"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePassword123"
  }'

# Get available books
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Borrow a book
curl -X POST http://localhost:8080/api/books/{BOOK_ID}/borrow \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "expiryMinutes": 1440
  }'

# Return a book
curl -X POST http://localhost:8080/api/books/{BOOK_ID}/return \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create a book (Admin)
curl -X POST http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "policy": "NORMAL"
  }'

# Get all books (Admin)
curl -X GET http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

## gRPC Services

### Proto Definition

The gRPC services are defined in `src/main/proto/book_service.proto`.

### Supported Operations

#### 1. GetBookById

```protobuf
rpc GetBookById(GetBookByIdRequest) returns (GetBookByIdResponse);
```

**Request:**
```json
{
  "id": "507f1f77bcf86cd799439012"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Book retrieved successfully",
  "book": {
    "id": "507f1f77bcf86cd799439012",
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "available": true,
    "borrowed_by": "",
    "borrowed_at": "",
    "expiry_at": "",
    "policy": "NORMAL"
  }
}
```

#### 2. ListBooks

```protobuf
rpc ListBooks(ListBooksRequest) returns (ListBooksResponse);
```

**Request:**
```json
{
  "available_only": true
}
```

**Response:**
```json
{
  "success": true,
  "message": "Books retrieved successfully",
  "books": [
    {
      "id": "507f1f77bcf86cd799439012",
      "title": "The Great Gatsby",
      "author": "F. Scott Fitzgerald",
      "available": true,
      "borrowed_by": "",
      "borrowed_at": "",
      "expiry_at": "",
      "policy": "NORMAL"
    }
  ],
  "count": 1
}
```

### gRPC Client Example (Kotlin)

```kotlin
import com.libmgmt.grpc.*
import io.grpc.ManagedChannelBuilder

fun main() {
    val channel = ManagedChannelBuilder.forAddress("localhost", 9091)
        .usePlaintext()
        .build()

    val stub = BookServiceGrpc.newBlockingStub(channel)

    // Get book by ID
    val bookRequest = GetBookByIdRequest.newBuilder()
        .setId("507f1f77bcf86cd799439012")
        .build()

    val bookResponse = stub.getBookById(bookRequest)
    println("Book: ${bookResponse.book.title}")

    // List books
    val listRequest = ListBooksRequest.newBuilder()
        .setAvailableOnly(true)
        .build()

    val listResponse = stub.listBooks(listRequest)
    println("Available books: ${listResponse.count}")

    channel.shutdown()
}
```

## Observability

### Prometheus Metrics

Access metrics at: **http://localhost:9000**

Key metrics:
- `http_requests_total` - Total HTTP requests
- `http_request_duration_seconds` - Request duration
- `jvm_memory_used_bytes` - JVM memory usage
- `jvm_threads_peak_threads` - Peak thread count
- `process_uptime_seconds` - Application uptime

### Health Check

```
GET http://localhost:8080/actuator/health

Response:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "hello": 1
      }
    }
  }
}
```

### Prometheus Endpoint

```
GET http://localhost:9090/actuator/prometheus
```

### Grafana Dashboards

Access Grafana: **http://localhost:3000**

Login: **admin / admin123**

Dashboards are auto-provisioned for:
- JVM Metrics
- HTTP Requests
- Database Performance
- Application Uptime

## Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test Class

```bash
./gradlew test --tests AuthServiceTest
```

### Generate Test Report

```bash
./gradlew test

# Report location:
# build/reports/tests/test/index.html
```

### Code Coverage

```bash
./gradlew jacocoTestReport

# Report location:
# build/reports/jacoco/test/html/index.html
```

### Test Types

#### Unit Tests
- `AuthServiceTest` - Authentication logic
- `BookServiceTest` - Book operations
- `JwtTokenProviderTest` - JWT token generation

#### Integration Tests
- Controller layer tests
- Repository layer tests
- End-to-end API tests

## Database Schema

### MongoDB Collections

#### Users Collection

```json
{
  "_id": ObjectId(),
  "name": "John Doe",
  "email": "john@example.com",
  "password": "$2a$10$...", // BCrypted
  "role": "USER", // USER or ADMIN
  "active": true,
  "createdAt": ISODate("2024-05-15T10:00:00Z"),
  "updatedAt": ISODate("2024-05-15T10:00:00Z")
}
```

#### Books Collection

```json
{
  "_id": ObjectId(),
  "title": "The Great Gatsby",
  "author": "F. Scott Fitzgerald",
  "available": true,
  "borrowedBy": null, // User ID when borrowed
  "borrowedAt": null, // Borrow timestamp
  "expiryAt": null, // Expiry timestamp
  "policy": "NORMAL", // NORMAL, EXPIRY, or END_OF_DAY
  "createdAt": ISODate("2024-05-15T10:00:00Z"),
  "updatedAt": ISODate("2024-05-15T10:00:00Z")
}
```

### Indexes

```bash
db.users.createIndex({ email: 1 }, { unique: true })
db.books.createIndex({ available: 1 })
db.books.createIndex({ borrowedBy: 1 })
db.books.createIndex({ expiryAt: 1 })
```

## Git Workflow

### Initial Setup

```bash
cd library-management-system
git init
git config user.email "your-email@example.com"
git config user.name "Your Name"
```

### Commit Structure

```bash
# Step 1: Authentication
git add .
git commit -m "feat: Add JWT authentication and user signup/login"
git tag step-1-auth

# Step 2: Book Management
git add .
git commit -m "feat: Add book management and borrow/return functionality"
git tag step-1-books

# Step 3: Borrow/Return
git add .
git commit -m "feat: Add book borrowing and return policies"
git tag step-1-borrow

# Step 4: Docker
git add .
git commit -m "chore: Add Docker and Docker Compose configuration"
git tag step-2-docker

# Step 5: Observability
git add .
git commit -m "chore: Add Prometheus and Grafana monitoring"
git tag step-2-observability

# Step 6: gRPC
git add .
git commit -m "feat: Add gRPC services for book operations"
git tag step-3-grpc
```

### View Tags

```bash
git tag -l
git show step-1-auth
```

## Troubleshooting

### MongoDB Connection Failed

```
Error: connect ECONNREFUSED 127.0.0.1:27017
```

**Solution:**
```bash
# Check if MongoDB is running
ps aux | grep mongod

# Start MongoDB
mongod

# Or with Docker
docker run -d -p 27017:27017 mongo:7.0
```

### JWT Token Invalid

```
Error: JWT token validation failed
```

**Solution:**
- Ensure `JWT_SECRET` is set correctly
- Check token expiration
- Verify Bearer prefix in Authorization header: `Bearer <token>`

### Port Already in Use

```
Error: Address already in use
```

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Or change port in application.yml
server:
  port: 8081
```

### Docker Compose Issues

```
Error: Cannot connect to Docker daemon
```

**Solution:**
```bash
# Ensure Docker is running
systemctl start docker

# Or on macOS
open /Applications/Docker.app

# Check Docker status
docker ps
```

### Build Failures

```
Error: could not find GradleVersion
```

**Solution:**
```bash
# Update Gradle wrapper
./gradlew wrapper --gradle-version=8.5

# Clean build
./gradlew clean build
```

### MongoDB Authentication Failed

```
Error: Authentication failed
```

**Solution in docker-compose.yml:**
```yaml
environment:
  MONGO_INITDB_ROOT_USERNAME: admin
  MONGO_INITDB_ROOT_PASSWORD: password123

spring:
  data:
    mongodb:
      uri: mongodb://admin:password123@mongodb:27017/library_db?authSource=admin
```

## Performance Tips

1. **Database Indexing**: Ensure all frequently queried fields have indexes
2. **Connection Pooling**: Configure MongoDB connection pool size
3. **Caching**: Implement Redis for frequently accessed data
4. **Load Testing**: Use tools like Apache JMeter to test performance
5. **Monitoring**: Regular check Prometheus/Grafana dashboards

## Security Considerations

1. **JWT Secret**: Use strong, randomly generated secret in production
2. **HTTPS**: Enable SSL/TLS in production
3. **Rate Limiting**: Implement rate limiting for API endpoints
4. **Input Validation**: All inputs are validated (already implemented)
5. **CORS**: Configure CORS appropriately for your frontend

## Production Deployment

### Environment Variables

```bash
export SPRING_DATA_MONGODB_URI=mongodb://user:password@prod-mongo:27017/library_db
export JWT_SECRET=your-very-secure-random-secret-key
export JWT_EXPIRATION=86400000
export SPRING_PROFILES_ACTIVE=prod
```

### Docker Production Build

```bash
docker build -t library-management-system:latest .
docker tag library-management-system:latest registry.example.com/library-management-system:latest
docker push registry.example.com/library-management-system:latest
```

### Kubernetes Deployment (Optional)

Create `k8s-deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: library-management-system
spec:
  replicas: 3
  selector:
    matchLabels:
      app: library-app
  template:
    metadata:
      labels:
        app: library-app
    spec:
      containers:
      - name: app
        image: registry.example.com/library-management-system:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATA_MONGODB_URI
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: mongodb-uri
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Support

For issues, questions, or suggestions:
- Create an issue on GitHub
- Send email to library@example.com
- Check existing issues and discussions

## Version History

- **1.0.0** (2024-05-16) - Initial release with complete feature set
  - JWT Authentication
  - Book Management
  - gRPC Services
  - Docker Setup
  - Prometheus + Grafana
  - Comprehensive Tests
  - Full Documentation

---

**Built with ❤️ using Kotlin, Spring Boot, and MongoDB**
