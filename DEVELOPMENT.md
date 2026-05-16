# Development Guide

## Local Development Environment Setup

### System Requirements

- **Java**: OpenJDK 21+ or Eclipse Temurin 21
- **MongoDB**: 7.0+
- **Docker**: 24.0+ (optional, for containerized setup)
- **Docker Compose**: 2.0+ (optional)
- **Git**: 2.40+
- **RAM**: Minimum 8GB, Recommended 16GB
- **Disk Space**: Minimum 5GB free space

### IDE Setup

#### IntelliJ IDEA (Recommended)

1. **Install**
   - Download from https://www.jetbrains.com/idea/download/
   - Install Community or Ultimate Edition

2. **Configure**
   - Open Project → Select `library-management-system` folder
   - IntelliJ auto-detects Gradle build system
   - Wait for indexing to complete

3. **Plugin Installation**
   - Kotlin Plugin (usually pre-installed)
   - Gradle Plugin (usually pre-installed)
   - Spring Boot Support (recommended)
   - MongoDB Plugin (recommended)

#### VS Code

1. **Install Extensions**
   ```
   - Extension Pack for Java (Microsoft)
   - Kotlin Language (Fwcd)
   - Spring Boot Extension Pack (Vmware)
   - MongoDB for VS Code (MongoDB)
   - Docker (Microsoft)
   ```

2. **Configure**
   - Create `.vscode/settings.json`:
   ```json
   {
     "[kotlin]": {
       "editor.defaultFormatter": "fwcd.kotlin",
       "editor.formatOnSave": true
     },
     "java.configuration.updateBuildConfiguration": "automatic",
     "java.server.launchMode": "Standard"
   }
   ```

### Database Setup

#### Option 1: Local MongoDB

```bash
# macOS
brew install mongodb-community
brew services start mongodb-community

# Ubuntu
sudo apt-get install -y mongodb-org
sudo systemctl start mongod

# Windows
# Download installer from https://www.mongodb.com/try/download/community
# Run installer and follow setup wizard
```

#### Option 2: Docker Container

```bash
docker run -d \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password123 \
  --name mongodb \
  mongo:7.0
```

### Running the Application

#### Command Line

```bash
# Navigate to project
cd library-management-system

# Build
./gradlew build

# Run
./gradlew bootRun

# Application starts at http://localhost:8080
```

#### IDE

**IntelliJ IDEA:**
1. Right-click on `LibraryManagementSystemApplication.kt`
2. Click "Run LibraryManagementSystemApplicationKt"
3. Or press `Shift + F10`

**VS Code:**
1. Open Command Palette (`Ctrl + Shift + P` / `Cmd + Shift + P`)
2. Type "Debug: Java"
3. Select "Spring Boot App"

### API Testing

#### Using cURL

```bash
# Test health check
curl http://localhost:8080/actuator/health

# Signup
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "Password123"
  }'
```

#### Using Postman

1. Download from https://www.postman.com/downloads/
2. Create new Collection
3. Import requests from `API_EXAMPLES.md`
4. Test endpoints

#### Using Swagger UI

- Open http://localhost:8080/swagger-ui.html
- All endpoints documented and testable from browser

### Debugging

#### IntelliJ IDEA Debugging

1. Set breakpoint by clicking line number
2. Run → Debug 'LibraryManagementSystemApplicationKt'
3. Inspect variables in debugger panel

#### Application Debugging

Add debug logs in application.yml:

```yaml
logging:
  level:
    com.libmgmt: DEBUG
    org.springframework.security: DEBUG
    org.springframework.data.mongodb: DEBUG
```

#### MongoDB Debugging

```bash
# Connect to MongoDB
mongosh

# List all databases
show dbs

# Use library_db
use library_db

# View collections
show collections

# Query users
db.users.find()

# Query books
db.books.find()
```

### Hot Reload Setup

#### Using Spring Boot DevTools

DevTools is already configured in `build.gradle.kts`.

1. **IntelliJ Configuration:**
   - Settings → Build, Execution, Deployment → Compiler
   - Check "Build project automatically"
   - Settings → Advanced Settings → "Allow auto-make to start..."

2. **VS Code:**
   - Install "Reload" extension
   - Changes auto-apply when saving files

### Code Formatting

#### Kotlin Formatting

```bash
# Format all Kotlin files
./gradlew ktlintFormat

# Or in IDE
Code → Reformat Code (Ctrl + Alt + L)
```

#### Import Organization

```bash
# Organize imports
./gradlew ktlintFormat --continue
```

### Testing

#### Run All Tests

```bash
./gradlew test
```

#### Run Specific Test Class

```bash
./gradlew test --tests AuthServiceTest
```

#### Run Single Test Method

```bash
./gradlew test --tests AuthServiceTest.signupSuccessfully
```

#### Debug Test

```bash
./gradlew test --debug-jvm
```

#### Generate Coverage Report

```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

### Build Optimization

#### Skip Tests

```bash
./gradlew build -x test
```

#### Parallel Build

```bash
./gradlew build --parallel
```

#### Build Cache

```bash
./gradlew build --build-cache
```

### Common Issues & Solutions

#### Issue: MongoDB Connection Refused

```
Error: connect ECONNREFUSED 127.0.0.1:27017
```

**Solution:**
```bash
# Check MongoDB is running
ps aux | grep mongod

# Start MongoDB
mongod

# Or Docker
docker start mongodb
```

#### Issue: Port Already in Use

```
Error: Address already in use: bind
```

**Solution:**
```bash
# Find process on port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Or change port in application.yml
server:
  port: 8081
```

#### Issue: Gradle Wrapper Permission Denied

```bash
chmod +x gradlew
```

#### Issue: Out of Memory

```bash
# Increase Gradle memory
export GRADLE_OPTS="-Xmx2048m"
./gradlew build
```

#### Issue: Spring Boot Not Starting

```bash
# Check logs
tail -f logs/application.log

# Run with debug
./gradlew bootRun --debug

# Check all dependencies
./gradlew dependencies
```

### Contributing Workflow

#### 1. Create Feature Branch

```bash
git checkout -b feature/user-profile
```

#### 2. Make Changes

```bash
# Edit files
code src/main/kotlin/com/libmgmt/...

# Format code
./gradlew ktlintFormat

# Add files
git add .
```

#### 3. Test Changes

```bash
# Run tests
./gradlew test

# Run specific test
./gradlew test --tests "*Test"

# Check test coverage
./gradlew jacocoTestReport
```

#### 4. Commit Changes

```bash
git commit -m "feat: Add user profile endpoint"
```

#### 5. Push & Create PR

```bash
git push origin feature/user-profile
```

### Code Style Guidelines

#### Kotlin Style

- Use `val` instead of `var` when possible
- Avoid mutable state
- Use data classes for POJOs
- Use sealed classes for type-safe enums
- Single responsibility principle

#### Example:

```kotlin
// ✅ Good
data class User(
    val id: String,
    val email: String,
    val name: String
)

// ❌ Bad
class User {
    var id: String? = null
    var email: String? = null
    var name: String? = null
}
```

#### Function Structure

```kotlin
// ✅ Good - Clear, concise, follows pattern
fun getUserById(userId: String): User {
    return userRepository.findById(userId)
        .orElseThrow { ResourceNotFoundException("User not found") }
}

// ❌ Bad - Overly complex
fun getUserById(userId: String): User? {
    try {
        val result = userRepository.findById(userId)
        return if (result.isPresent) result.get() else null
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}
```

### Performance Profiling

#### Using JProfiler (Commercial)

```bash
# Download from https://www.jprofiler.com/
# Configure IDE integration
# Run application with profiler
```

#### Using YourKit Profiler (Trial Available)

```bash
# Download from https://www.yourkit.com/
# Similar setup to JProfiler
```

### Documentation

#### JavaDoc

```kotlin
/**
 * Borrows a book for the user.
 *
 * @param bookId the ID of the book to borrow
 * @param userId the ID of the user borrowing the book
 * @param request borrow request with optional expiry
 * @return BorrowBookResponse with borrow details
 * @throws BookNotAvailableException if book is not available
 * @throws ResourceNotFoundException if book or user not found
 */
fun borrowBook(
    bookId: String,
    userId: String,
    request: BorrowBookRequest
): BorrowBookResponse
```

### Git Configuration

```bash
# Set git user
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"

# Create global gitignore
git config --global core.excludesfile ~/.gitignore_global

# Configure line endings
git config --global core.safecrlf true
```

### Useful Gradle Tasks

```bash
# List all tasks
./gradlew tasks

# Show project info
./gradlew projects

# Check updates
./gradlew dependencyUpdates

# Security scan
./gradlew dependencyCheckAnalyze
```

### Terminal Shortcuts

```bash
# Alias for gradlew
alias gradle='./gradlew'

# Quick build
gradle build -x test

# Quick run
gradle bootRun

# Quick test
gradle test
```

### Additional Resources

- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [gRPC Kotlin Documentation](https://grpc.io/docs/languages/kotlin/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc7519)
