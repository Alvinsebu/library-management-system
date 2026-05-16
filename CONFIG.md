# Library Management System - Configuration Documentation

## Environment Configuration

### Development Environment

Create `src/main/resources/application-dev.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017
      database: library_db_dev

jwt:
  secret: "dev-secret-key-not-for-production"
  expiration: 86400000

server:
  port: 8080
  servlet:
    context-path: /api

logging:
  level:
    root: INFO
    com.libmgmt: DEBUG
```

### Production Environment

Create `src/main/resources/application-prod.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://user:password@prod-cluster.mongodb.net/library_db?retryWrites=true&w=majority
      database: library_db

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:86400000}

server:
  port: 8080
  servlet:
    context-path: /api
  compression:
    enabled: true
    min-response-size: 1024

logging:
  level:
    root: WARN
    com.libmgmt: INFO

management:
  endpoints:
    web:
      exposure:
        include: health,prometheus,info,metrics
```

## Running with Specific Profile

```bash
# Development
./gradlew bootRun --args='--spring.profiles.active=dev'

# Production
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## Database Configuration

### MongoDB Atlas (Cloud)

```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://username:password@cluster.mongodb.net/library_db?retryWrites=true&w=majority
```

### MongoDB Local

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017
      database: library_db
```

## Security Configuration

### CORS Configuration

Update `SecurityConfig.kt` for specific origins:

```kotlin
fun corsConfigurationSource(): CorsConfigurationSource {
    val configuration = CorsConfiguration().apply {
        allowedOrigins = listOf(
            "http://localhost:3000",
            "https://app.example.com"
        )
        allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        allowedHeaders = listOf("*")
        maxAge = 3600L
        allowCredentials = true
    }
    val source = UrlBasedCorsConfigurationSource()
    source.registerCorsConfiguration("/**", configuration)
    return source
}
```

## Application Properties Reference

| Property | Value | Description |
|----------|-------|-------------|
| `spring.application.name` | library-management-system | Application name |
| `spring.data.mongodb.uri` | mongodb://localhost:27017 | MongoDB connection URI |
| `spring.data.mongodb.database` | library_db | Database name |
| `jwt.secret` | your-secret-key | JWT signing secret |
| `jwt.expiration` | 86400000 | Token expiration (ms) |
| `server.port` | 8080 | Server port |
| `server.servlet.context-path` | /api | API context path |
| `management.endpoints.web.exposure.include` | health,prometheus | Actuator endpoints |

## Feature Flags

### Enable/Disable Scheduling

In `LibraryManagementSystemApplication.kt`:

```kotlin
@SpringBootApplication
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true", matchIfMissing = true)
@EnableScheduling
class LibraryManagementSystemApplication
```

In `application.yml`:

```yaml
app:
  scheduling:
    enabled: true
```

## Monitoring Configuration

### Custom Metrics

Add custom metrics in your services:

```kotlin
import io.micrometer.core.instrument.MeterRegistry

@Service
class BookService(
    private val meterRegistry: MeterRegistry
) {
    fun borrowBook(...) {
        meterRegistry.counter("books.borrowed").increment()
    }
}
```

## Database Backup

### MongoDB Backup

```bash
# Backup
mongodump --uri="mongodb://localhost:27017" --out=./backup

# Restore
mongorestore ./backup
```

### Docker Backup

```bash
# Backup MongoDB in Docker
docker exec library-mongodb mongodump --out=/data/backup

# Copy from container
docker cp library-mongodb:/data/backup ./backup
```
