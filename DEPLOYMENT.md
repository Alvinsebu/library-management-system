# Deployment Guide

## Pre-Deployment Checklist

- [ ] All tests passing: `./gradlew test`
- [ ] Code formatted: `./gradlew ktlintFormat`
- [ ] Security scan completed
- [ ] Dependencies updated: `./gradlew dependencyUpdates`
- [ ] Documentation updated
- [ ] Version bumped in `build.gradle.kts`
- [ ] Environment variables configured
- [ ] Database migrations tested
- [ ] Monitoring setup verified
- [ ] Backup strategy in place

## Building for Deployment

### Build JAR

```bash
./gradlew clean bootJar

# Output: build/libs/library-management-system-1.0.0.jar
```

### Build Docker Image

```bash
# Build image
docker build -t library-management-system:1.0.0 .

# Tag for registry
docker tag library-management-system:1.0.0 \
  registry.example.com/library-management-system:1.0.0

# Push to registry
docker push registry.example.com/library-management-system:1.0.0
```

## Deployment Methods

### Method 1: Direct JAR Deployment

#### Prerequisites
- Java 21+ installed
- MongoDB running
- Environment variables set

#### Deploy

```bash
# Copy JAR to server
scp build/libs/library-management-system-1.0.0.jar \
  user@server:/opt/apps/

# SSH into server
ssh user@server

# Create systemd service
sudo tee /etc/systemd/system/library-app.service << EOF
[Unit]
Description=Library Management System
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/apps
Environment="SPRING_DATA_MONGODB_URI=mongodb://user:pass@mongo:27017/library_db"
Environment="JWT_SECRET=your-secret-key"
ExecStart=/usr/bin/java -jar library-management-system-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Enable and start service
sudo systemctl enable library-app
sudo systemctl start library-app

# Check status
sudo systemctl status library-app
sudo journalctl -u library-app -f
```

### Method 2: Docker Deployment

#### Single Container

```bash
docker run -d \
  --name library-app \
  -p 8080:8080 \
  -p 9090:9090 \
  -e SPRING_DATA_MONGODB_URI=mongodb://user:password@mongo-host:27017/library_db \
  -e JWT_SECRET=your-secret-key \
  -e SPRING_PROFILES_ACTIVE=prod \
  registry.example.com/library-management-system:1.0.0
```

#### Docker Compose Production

Create `docker-compose.prod.yml`:

```yaml
version: '3.8'

services:
  app:
    image: registry.example.com/library-management-system:1.0.0
    container_name: library-app
    restart: always
    ports:
      - "8080:8080"
      - "9090:9090"
      - "9091:9091"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://admin:${MONGO_PASSWORD}@mongodb:27017/library_db?authSource=admin
      JWT_SECRET: ${JWT_SECRET}
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      - mongodb
      - prometheus
    networks:
      - library-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  mongodb:
    image: mongo:7.0
    container_name: library-mongodb
    restart: always
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: ${MONGO_PASSWORD}
      MONGO_INITDB_DATABASE: library_db
    volumes:
      - mongodb_data:/data/db
    networks:
      - library-network
    healthcheck:
      test: echo 'db.runCommand("ping").ok' | mongosh localhost:27017/library_db -u admin -p ${MONGO_PASSWORD} --quiet
      interval: 10s
      timeout: 5s
      retries: 5

  prometheus:
    image: prom/prometheus:latest
    container_name: library-prometheus
    restart: always
    ports:
      - "9000:9090"
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    networks:
      - library-network

  grafana:
    image: grafana/grafana:latest
    container_name: library-grafana
    restart: always
    ports:
      - "3000:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD}
    volumes:
      - grafana_data:/var/lib/grafana
    networks:
      - library-network

volumes:
  mongodb_data:
    driver: local
  prometheus_data:
    driver: local
  grafana_data:
    driver: local

networks:
  library-network:
    driver: bridge
```

Deploy:

```bash
# Set environment variables
export MONGO_PASSWORD=your-mongo-password
export JWT_SECRET=your-jwt-secret
export GRAFANA_PASSWORD=your-grafana-password

# Start services
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose -f docker-compose.prod.yml logs -f app
```

### Method 3: Kubernetes Deployment

#### Prerequisites
- Kubernetes cluster (1.24+)
- kubectl configured
- Container registry access

#### Deployment Files

**namespace.yaml:**
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: library-management
```

**configmap.yaml:**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: library-management
data:
  SPRING_PROFILES_ACTIVE: "prod"
  SERVER_PORT: "8080"
```

**secret.yaml:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
  namespace: library-management
type: Opaque
stringData:
  SPRING_DATA_MONGODB_URI: "mongodb://admin:password@mongodb:27017/library_db?authSource=admin"
  JWT_SECRET: "your-secret-key"
```

**deployment.yaml:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: library-app
  namespace: library-management
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
        image: registry.example.com/library-management-system:1.0.0
        ports:
        - containerPort: 8080
          name: http
        - containerPort: 9090
          name: metrics
        - containerPort: 9091
          name: grpc
        envFrom:
        - configMapRef:
            name: app-config
        - secretRef:
            name: app-secrets
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
        resources:
          requests:
            cpu: "250m"
            memory: "512Mi"
          limits:
            cpu: "500m"
            memory: "1Gi"
```

**service.yaml:**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: library-app-service
  namespace: library-management
spec:
  type: LoadBalancer
  selector:
    app: library-app
  ports:
  - name: http
    port: 80
    targetPort: 8080
  - name: metrics
    port: 9090
    targetPort: 9090
  - name: grpc
    port: 9091
    targetPort: 9091
```

#### Deploy to Kubernetes

```bash
# Create namespace and resources
kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

# Check deployment
kubectl get deployment -n library-management
kubectl get pods -n library-management
kubectl get svc -n library-management

# View logs
kubectl logs -n library-management -l app=library-app -f

# Port forward for testing
kubectl port-forward -n library-management svc/library-app-service 8080:80
```

## Database Migration

### MongoDB to Production

```bash
# Backup development database
mongodump --uri="mongodb://localhost:27017" --out=./backup

# Restore to production
mongorestore --uri="mongodb://user:password@prod-mongo:27017" ./backup/library_db
```

### Database Versioning (Optional)

Use migration tools:
- Mongock: Spring-based MongoDB migration
- Flyway: Database migration tool

## SSL/TLS Configuration

### Using Let's Encrypt with Nginx

```bash
# Install certbot
sudo apt-get install certbot python3-certbot-nginx

# Generate certificate
sudo certbot certonly --standalone -d api.example.com

# Configure application
spring:
  application:
    name: library-management-system
  jpa:
    hibernate:
      ddl-auto: validate

server:
  ssl:
    key-store: /etc/letsencrypt/live/api.example.com/keystore.p12
    key-store-password: your-password
    key-store-type: PKCS12
```

### Docker SSL Setup

```yaml
version: '3.8'
services:
  nginx:
    image: nginx:latest
    ports:
      - "443:443"
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - /etc/letsencrypt:/etc/letsencrypt
    depends_on:
      - app

  app:
    image: library-management-system:latest
    expose:
      - "8080"
```

## Monitoring & Logging

### Log Aggregation (ELK Stack)

```yaml
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.0.0
    environment:
      - discovery.type=single-node
    ports:
      - "9200:9200"

  logstash:
    image: docker.elastic.co/logstash/logstash:8.0.0
    volumes:
      - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf
    depends_on:
      - elasticsearch

  kibana:
    image: docker.elastic.co/kibana/kibana:8.0.0
    ports:
      - "5601:5601"
    depends_on:
      - elasticsearch
```

### Alerting with AlertManager

```yaml
alerting:
  alertmanagers:
    - static_configs:
        - targets:
            - localhost:9093
```

## Performance Tuning

### JVM Tuning

```bash
export JAVA_OPTS="-Xmx2g -Xms2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
java -jar library-management-system-1.0.0.jar
```

### MongoDB Tuning

```javascript
// Increase oplog size
rs.reconfigureReplicaSet({
  settings: {
    oplogSizeMB: 10000
  }
})

// Create indexes
db.users.createIndex({ email: 1 }, { unique: true })
db.books.createIndex({ available: 1 })
db.books.createIndex({ borrowedBy: 1 })
```

## Backup & Recovery

### Automated Backup Script

```bash
#!/bin/bash

BACKUP_DIR="/backups/library-db"
DATE=$(date +%Y%m%d_%H%M%S)

# Backup MongoDB
mongodump \
  --uri="mongodb://user:password@mongo:27017" \
  --out="$BACKUP_DIR/backup_$DATE"

# Compress backup
tar -czf "$BACKUP_DIR/backup_$DATE.tar.gz" "$BACKUP_DIR/backup_$DATE"
rm -rf "$BACKUP_DIR/backup_$DATE"

# Keep only last 30 days
find "$BACKUP_DIR" -name "backup_*.tar.gz" -mtime +30 -delete

# Upload to S3 (optional)
aws s3 cp "$BACKUP_DIR/backup_$DATE.tar.gz" \
  s3://library-backups/
```

## Health Checks

### Production Health Check

```bash
# Application health
curl -s http://localhost:8080/actuator/health | jq .

# Database health
curl -s http://localhost:8080/actuator/health/db | jq .

# Ready check
curl -s http://localhost:8080/actuator/health/readiness | jq .
```

## Rollback Strategy

### Version Management

```bash
# Tag releases
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0

# Keep multiple versions available
docker pull registry.example.com/library-management-system:1.0.0
docker pull registry.example.com/library-management-system:1.0.1
```

### Quick Rollback

```bash
# Stop current version
docker stop library-app

# Start previous version
docker run -d \
  --name library-app \
  registry.example.com/library-management-system:1.0.0
```

## Troubleshooting Production Issues

### High Memory Usage

```bash
# Check JVM stats
jstat -gc -h10 <process_id> 1000

# Adjust heap size
export JAVA_OPTS="-Xmx1g -Xms1g"
```

### Database Timeout

```bash
# Check connection pool
curl http://localhost:9090/actuator/metrics | grep database

# Increase pool size
spring:
  data:
    mongodb:
      max-pool-size: 100
```

### Slow Queries

```bash
# Enable query logging
db.setProfilingLevel(1, { slowms: 100 })

# View slow queries
db.system.profile.find().pretty()
```

---

**Last Updated**: 2024-05-16
**Version**: 1.0.0
