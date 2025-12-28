# Docker Setup for Smart Clinic

This document explains how to run the Smart Clinic application using Docker Compose.

## Prerequisites

- Docker Desktop installed (includes Docker Compose)
- At least 4GB of RAM available for Docker
- Ports 3306, 27017, and 8080 available on your machine

## Quick Start

### 1. Start all services

```bash
docker-compose up -d
```

This will start:
- **MySQL** on port 3306
- **MongoDB** on port 27017
- **Spring Boot App** on port 8080

### 2. Check service status

```bash
docker-compose ps
```

### 3. View logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f app
docker-compose logs -f mysql
docker-compose logs -f mongodb
```

### 4. Access the application

- **Application**: http://localhost:8080
- **MySQL**: `localhost:3306` (use any MySQL client)
- **MongoDB**: `localhost:27017` (use MongoDB Compass or mongosh)

## Management Commands

### Stop all services

```bash
docker-compose down
```

### Stop and remove all data (volumes)

```bash
docker-compose down -v
```

### Restart a specific service

```bash
docker-compose restart app
docker-compose restart mysql
docker-compose restart mongodb
```

### Rebuild the application

```bash
docker-compose up -d --build app
```

### Execute commands in containers

```bash
# MySQL
docker-compose exec mysql mysql -uroot -prootpassword cms

# MongoDB
docker-compose exec mongodb mongosh -u root -p rootpassword --authenticationDatabase admin

# App shell
docker-compose exec app sh
```

## Database Connections

### MySQL Connection

```properties
Host: localhost
Port: 3306
Database: cms
Username: root
Password: rootpassword
```

### MongoDB Connection

```properties
Host: localhost
Port: 27017
Database: prescriptions
Username: root
Password: rootpassword
Auth Database: admin
```

Connection String:
```
mongodb://root:rootpassword@localhost:27017/prescriptions?authSource=admin
```

## Troubleshooting

### Port already in use

If you get port conflicts, you can change the ports in `docker-compose.yml`:

```yaml
ports:
  - "3307:3306"  # Use 3307 instead of 3306
```

### Application fails to start

1. Check if databases are healthy:
   ```bash
   docker-compose ps
   ```

2. Check application logs:
   ```bash
   docker-compose logs app
   ```

3. Restart the application:
   ```bash
   docker-compose restart app
   ```

### Database connection errors

1. Ensure services are running:
   ```bash
   docker-compose ps
   ```

2. Check health status:
   ```bash
   docker inspect smart-clinic-mysql | grep -A 5 Health
   docker inspect smart-clinic-mongodb | grep -A 5 Health
   ```

3. Restart databases:
   ```bash
   docker-compose restart mysql mongodb
   ```

### Clean slate restart

```bash
# Stop everything
docker-compose down -v

# Remove old images
docker-compose down --rmi all

# Start fresh
docker-compose up -d --build
```

## Development Workflow

### 1. Local development with Docker databases

Run only MySQL and MongoDB:

```bash
docker-compose up -d mysql mongodb
```

Then run the Spring Boot app from your IDE with this configuration in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cms?useSSL=false
spring.datasource.username=root
spring.datasource.password=rootpassword

spring.data.mongodb.uri=mongodb://root:rootpassword@localhost:27017/prescriptions?authSource=admin
```

### 2. Full Docker deployment

Run everything in Docker:

```bash
docker-compose up -d
```

### 3. Update application code

After making code changes:

```bash
# Rebuild and restart app
docker-compose up -d --build app
```

## Data Persistence

All data is persisted in Docker volumes:
- `mysql_data`: MySQL database files
- `mongodb_data`: MongoDB database files

To backup data:

```bash
# MySQL backup
docker-compose exec mysql mysqldump -uroot -prootpassword cms > backup.sql

# MongoDB backup
docker-compose exec mongodb mongodump --uri="mongodb://root:rootpassword@localhost:27017/prescriptions?authSource=admin" --out=/tmp/backup
docker cp smart-clinic-mongodb:/tmp/backup ./mongodb-backup
```

## Production Considerations

For production deployment:

1. **Change all passwords** in `.env` file
2. **Enable SSL/TLS** for database connections
3. **Use secrets management** instead of environment variables
4. **Enable authentication** for MongoDB
5. **Configure backup strategy**
6. **Set up monitoring and logging**
7. **Use reverse proxy** (nginx) for HTTPS
8. **Implement rate limiting**

## Network Architecture

All services run on the `smart-clinic-network` bridge network, allowing them to communicate using service names (mysql, mongodb, app) as hostnames.

```
┌─────────────────────────────────────────┐
│  smart-clinic-network (bridge)          │
│                                         │
│  ┌──────────┐  ┌──────────┐  ┌───────┐  │
│  │  MySQL   │  │ MongoDB  │  │  App  │  │
│  │  :3306   │  │  :27017  │  │ :8080 │  │
│  └──────────┘  └──────────┘  └───────┘  │
│       ↓              ↓            ↓     │
└───────┼──────────────┼────────────┼─────┘
        │              │            │
    localhost:3306  localhost:27017 localhost:8080
```

## Environment Variables

Copy `.env.example` to `.env` and customize:

```bash
cp .env.example .env
```

Then edit `.env` with your values. Docker Compose will automatically load these variables.

## Health Checks

All services have health checks configured:

- **MySQL**: Pings database every 10s
- **MongoDB**: Runs ping command every 10s
- **App**: Depends on healthy databases before starting

## Support

For issues:
1. Check logs: `docker-compose logs -f`
2. Verify all services are running: `docker-compose ps`
3. Check network connectivity: `docker network inspect smart-clinic-network`
4. Restart services: `docker-compose restart`
