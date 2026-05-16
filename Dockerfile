FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY . .

# Fix Windows CRLF line endings in gradlew and set execute permissions
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]