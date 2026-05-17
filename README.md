# Library Management System

A polished, production-ready backend for library management built with **Kotlin**, **Spring Boot**, **MongoDB**, **JWT**, **gRPC**, **Prometheus**, and **Docker**.

![Project Architecture](docs/screenshots/architecture.png)

## Table of Contents

- [Summary](#summary)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Swagger Usage](#swagger-usage)
- [Docker Compose Setup](#docker-compose-setup)
- [MongoDB Setup](#mongodb-setup)
- [Observability](#observability)
- [gRPC Overview](#grpc-overview)
- [Testing](#testing)
- [Future Improvements](#future-improvements)
- [Release Tags](#release-tags)
- [Additional Resources](#additional-resources)

## Summary

This backend provides a secure, maintainable library management API that supports:

- User registration and login
- JWT-secured REST endpoints
- Book borrowing and return workflows
- Book policy rules (`NORMAL`, `EXPIRY`, `END_OF_DAY`)
- Scheduled auto-return processing
- gRPC service support
- Prometheus/Grafana observability
- Docker Compose deployment

The implementation was improved for documentation, architecture clarity, and developer usability without changing existing business logic, API contract, or runtime behavior.


## Running the Library Management System
Prerequisites

Before starting the application, make sure the following are installed:

Docker Desktop
Docker Compose
Java 21 (optional for local development)
VS Code or IntelliJ IDEA (recommended)
Step 1 — Start Docker Desktop
 - Open Docker Desktop and wait until: 'Engine running'
Step 2 — Open Terminal
 - Open PowerShell or terminal inside the project folder
Step 3 — Build and Start the Application
 - docker compose up --build
   
   This command will:

Build the Kotlin Spring Boot application
Start MongoDB
Start Prometheus
Start Grafana
Start the backend API server

Step 4 — Access the Application
http://localhost:8080/swagger-ui.html
grafana dashboard - http://localhost:3000

Step 5 - Stopping the Application
CTRL + C
docker compose down

## Key Features

- Secure JWT authentication and authorization
- Clean REST API design with consistent response payloads
- MongoDB-backed persistence with auth-enabled access
- Automatic book return workflows for borrow policies
- Swagger/OpenAPI endpoint exploration
- gRPC interface for high-performance integrations
- Application health and metrics via Spring Boot Actuator
- Dockerized local development and monitoring stack

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin 1.9.20 |
| Framework | Spring Boot 3.2.0 |
| Build Tool | Gradle (Kotlin DSL) |
| Database | MongoDB 7.x |
| Auth | JWT (JJWT) |
| RPC | gRPC + Protobuf |
| Monitoring | Micrometer + Prometheus + Grafana |
| Docs | Swagger/OpenAPI |
| Containerization | Docker / Docker Compose |
| Testing | JUnit 5, Kotest, MockK |

## Architecture

The application is organized into distinct layers to preserve separation of concerns and support maintainability:

- **Controller**: handles REST request routing and response wrapping
- **Service**: implements business rules and transaction flows
- **Repository**: performs MongoDB data access
- **Model / DTO**: defines domain entities and API payloads
- **Security**: manages JWT validation and authentication filter
- **Scheduler**: executes periodic auto-return tasks
- **gRPC**: exposes a secondary API surface for external clients

![API Flow](docs/screenshots/api-flow.png)

### High-level data flow

1. Client sends REST request or gRPC call
2. Authentication filter validates JWT and attaches user context
3. Controller delegates to service layer
4. Service applies domain rules and persists data via repository
5. Response is returned in a consistent `ApiResponse` wrapper

## Project Structure

```text
library-management-system/
├── src/
│   ├── main/
│   │   ├── kotlin/com/libmgmt/
│   │   │   ├── config/        # Spring and gRPC configuration
│   │   │   ├── controller/    # REST controllers
│   │   │   ├── service/       # Business logic
│   │   │   ├── repository/    # MongoDB access
│   │   │   ├── model/         # Domain models
│   │   │   ├── dto/           # Request/response DTOs
│   │   │   ├── security/      # JWT and auth filter
│   │   │   ├── scheduler/     # Scheduled tasks
│   │   │   ├── grpc/          # gRPC implementation
│   │   │   ├── exception/     # Centralized error handling
│   │   │   ├── util/          # Utility classes
│   │   │   └── LibraryManagementSystemApplication.kt
│   │   ├── proto/             # gRPC protobuf definitions
│   │   └── resources/
│   │       └── application.yml
│   └── test/                  # Unit and integration tests
├── docker/                    # MongoDB initialization scripts
├── prometheus/                # Monitoring configuration
├── Dockerfile
├── docker-compose.yml
├── build.gradle.kts
└── README.md
```

## Getting Started

### Quick start

```bash
cd library-management-system
./gradlew clean build
./gradlew bootRun
```

Default application URL:

```text
http://localhost:8080/api
```

### Recommended workflow

1. Review `src/main/resources/application.yml`
2. Configure MongoDB credentials and JWT settings
3. Start services locally with `./gradlew bootRun`
4. Explore endpoints using Swagger UI
5. Run tests with `./gradlew test`

## Configuration

### Application properties

The main configuration file is `src/main/resources/application.yml`.

Key settings include:

- `spring.data.mongodb.uri` — MongoDB connection URI
- `spring.data.mongodb.database` — MongoDB database name
- `jwt.secret` — JWT signing secret
- `jwt.expiration` — JWT expiration in milliseconds
- `grpc.server.port` — gRPC server port
- `management.server.port` — actuator/metrics port

### Environment variables

The Docker Compose stack uses environment variables for the app service:

- `SPRING_DATA_MONGODB_URI`
- `SPRING_DATA_MONGODB_DATABASE`
- `SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE`
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE`

## API Documentation

This backend exposes the following primary REST endpoints under `/api`.

### Authentication

#### Register

- `POST /api/auth/signup`
- Request body:

```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "Password123!"
}
```

- Success response:

```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": "...",
    "name": "Jane Doe",
    "email": "jane@example.com",
    "role": "USER",
    "token": "...",
    "message": "User registered successfully"
  }
}
```

#### Login

- `POST /api/auth/login`
- Request body:

```json
{
  "email": "jane@example.com",
  "password": "Password123!"
}
```

- Success response:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": "...",
    "name": "Jane Doe",
    "email": "jane@example.com",
    "role": "USER",
    "token": "...",
    "message": "Login successful"
  }
}
```

### Book Management

#### Get available books

- `GET /api/books`

#### Get book by ID

- `GET /api/books/{id}`

#### Borrow a book

- `POST /api/books/{id}/borrow`
- Request body example:

```json
{
  "expiryMinutes": 120
}
```

#### Return a book

- `POST /api/books/{id}/return`

#### Get borrowed books for current user

- `GET /api/books/user/borrowed`

### API examples

#### Register example

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"jane@example.com","password":"Password123!"}'
```

#### Login example

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@example.com","password":"Password123!"}'
```

#### Borrow book example

```bash
curl -X POST http://localhost:8080/api/books/{bookId}/borrow \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{"expiryMinutes": 120}'
```

## Swagger Usage

Swagger UI is enabled and accessible at:

```text
http://localhost:8080/swagger-ui.html
```

Use the `Authorize` button inside Swagger UI to enter a bearer token in the format:

```text
Bearer <JWT_TOKEN>
```

The API documentation is auto-generated from controller metadata and DTO annotations.

![Swagger UI placeholder](docs/screenshots/swagger-ui.png)

## Docker Compose Setup

The repository includes a `docker-compose.yml` file that starts:

- `mongodb` — MongoDB database
- `app` — Kotlin Spring Boot backend
- `prometheus` — metrics engine
- `grafana` — visualization dashboard

### Start full stack

```bash
docker compose up --build
```

### Shutdown

```bash
docker compose down
```

### Health checks

- App health endpoint: `http://localhost:8080/actuator/health`
- Prometheus UI: `http://localhost:9000`
- Grafana UI: `http://localhost:3000`

## MongoDB Setup

### Local MongoDB

Use a local MongoDB installation or Docker Compose.

The project uses an authenticated MongoDB URI with `authSource=admin`.

Example local URI:

```text
mongodb://admin:password123@localhost:27017/library_db?authSource=admin
```

### Docker Compose MongoDB

The Compose service mounts `docker/init-mongo.js` to initialize the database during startup.

## Observability

The application exposes actuator metrics and Prometheus integration:

- Actuator health and metrics endpoints
- Prometheus scraping configured in `prometheus/prometheus.yml`
- Grafana dashboards provisioned in `prometheus/grafana-provisioning`

Observability endpoints:

- `http://localhost:8080/actuator/health`
- `http://localhost:8080/actuator/prometheus`
- `http://localhost:8080/actuator/info`

## gRPC Overview

The backend also exposes a gRPC interface for book operations.

- Protobuf source: `src/main/proto/book_service.proto`
- Generated client code: `build/generated/source/proto/main/`
- gRPC port: `9091`

Use gRPC to integrate with high-performance non-HTTP clients while preserving the same business logic.

## Testing

Execute the test suite with:

```bash
./gradlew test
```

Verify build and tests remain green after documentation or comment-only updates.

## Future Improvements

Potential enhancements for future releases:

- Separate admin and user roles more explicitly in the API
- Add dedicated integration tests for gRPC endpoints
- Implement request rate limiting and advanced security policies
- Add production-grade secret management and environment profiles
- Improve Grafana dashboards with custom library metrics
- Add API versioning and feature toggle support

## Release Tags

Suggested git tags for organized releases:

```bash
git tag -a v1.0.0-http-apis -m "Step 1: HTTP API implementation"
git tag -a v1.1.0-docker-observability -m "Step 2: Docker and observability setup"
git tag -a v1.2.0-grpc -m "Step 3: gRPC support"
```

## Additional Resources

For extended configuration, deployment, and development notes, use the repository's existing documentation files such as `CONFIG.md`, `DEPLOYMENT.md`, and `DEVELOPMENT.md`.
