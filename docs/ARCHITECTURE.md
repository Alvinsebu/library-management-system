# Architecture Overview

## Core Layers

- **Controller Layer**
  - `AuthController.kt` handles user login/signup.
  - `BookController.kt` handles book listing, borrowing, returning, and user book queries.

- **Service Layer**
  - `AuthService.kt` encapsulates authentication logic, password hashing, and token generation.
  - `BookService.kt` encapsulates book lifecycle operations and business rules.

- **Repository Layer**
  - `UserRepository.kt` and `BookRepository.kt` manage MongoDB persistence.

- **Security Layer**
  - `JwtTokenProvider.kt` generates and validates JWT tokens.
  - `JwtAuthenticationFilter.kt` reads bearer tokens and injects authentication into Spring Security.

- **Exception Handling**
  - `GlobalExceptionHandler.kt` converts exceptions into consistent API error responses.

## Integration Points

- **MongoDB**
  - Configured through `application.yml` and Docker Compose.
  - Uses username/password authentication with `authSource` set to `admin`.

- **gRPC**
  - Configured in `application.yml` and served via `GrpcServerConfig.kt`.
  - Protobuf definitions live in `src/main/proto/book_service.proto`.

- **OpenAPI / Swagger**
  - Exposed by Springdoc at `/swagger-ui.html` and `/v3/api-docs`.

- **Observability**
  - Metrics exposed by Spring Boot Actuator under `/actuator/prometheus`.

## Data Flow

1. Client calls a REST endpoint or gRPC method.
2. Request enters controller or gRPC service.
3. Security filter validates JWT for protected endpoints.
4. Services perform domain logic and access repositories.
5. Responses are returned as DTOs or proto messages.

## Why This Structure

- Keeps business logic isolated from transport concerns.
- Preserves existing API behavior while improving maintainability.
- Supports both HTTP and gRPC interfaces without altering domain rules.
