# Quick Test Guide - Gradle Wrapper Fix

## Test 1: Verify Local Setup (Windows)

Run these commands in PowerShell in your project root:

```powershell
# Test gradlew exists and is executable
.\gradlew --version

# This will download Gradle 8.5 and show version info
# Takes 1-2 minutes on first run
```

**Expected output:**
```
Gradle 8.5
...
```

## Test 2: Build Locally (Optional - for faster Docker builds)

```powershell
# Build project locally with proper tests
.\gradlew build

# Or skip tests (faster)
.\gradlew build -x test
```

## Test 3: Docker Build

```powershell
# From project root directory
docker compose up --build
```

**What happens:**
1. Docker pulls eclipse-temurin:21-jdk image
2. Copies project files (including gradle/wrapper/gradle-wrapper.properties)
3. Fixes line endings on gradlew (sed command)
4. Sets execute permissions (chmod +x)
5. Runs `./gradlew build -x test` inside container
6. Downloads Gradle 8.5 inside container (~200MB)
7. Builds your Kotlin/Spring Boot application
8. Creates JAR artifact
9. Runs final container with JRE image

## Key Points

✓ **gradlew** - Proper Unix shell script (160 lines)
✓ **gradlew.bat** - Proper Windows batch script
✓ **gradle-wrapper.properties** - Specifies Gradle 8.5
✓ **gradle/wrapper/gradle-wrapper.jar** - Will be auto-downloaded on first run
✓ **Dockerfile** - Handles line endings + permissions

## If Tests Fail

1. **"gradlew not found"**
   ```powershell
   # Verify file exists
   ls -Path .\gradlew
   
   # Make it executable
   chmod +x gradlew
   ```

2. **"Permission denied"**
   ```powershell
   # On Unix/Mac
   chmod +x gradlew
   ```

3. **Docker image download issues**
   ```powershell
   # Ensure docker daemon is running
   docker ps
   
   # Clear image cache if needed
   docker image prune
   ```

## Performance Notes

- **First build**: ~3-5 minutes (downloads Gradle + dependencies)
- **Subsequent builds**: ~1-2 minutes (uses cached Gradle + dependencies)
- **Docker builds**: First build downloads Gradle inside container (~3-5 min), subsequent builds reuse layer cache

---

Run `docker compose up --build` now. Your application should build successfully! 🚀
