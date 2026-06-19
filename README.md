# Scalable URL Shortener

A production-style URL shortener backend built with Spring Boot, PostgreSQL, Redis, JWT authentication, analytics tracking, Swagger API documentation, Docker, and GitHub Actions CI.

The project allows authenticated users to create short URLs, use custom aliases, redirect through short links, track click analytics, apply Redis-backed caching, and enforce rate limits.

---

## Features

- User registration and login
- JWT-based authentication
- Create shortened URLs
- Custom alias support
- Redirect short URLs to original URLs
- URL ownership validation
- Soft delete / deactivate URLs
- Click analytics tracking
- Recent click history
- Redis caching for fast redirects
- Redis-backed rate limiting
- PostgreSQL persistence
- Swagger / OpenAPI documentation
- Dockerized Spring Boot backend
- Docker Compose setup for backend, PostgreSQL, and Redis
- GitHub Actions CI pipeline
- Unit tests with JUnit and Mockito

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot |
| Database | PostgreSQL 16 |
| Cache | Redis 7 |
| Authentication | JWT |
| ORM | Spring Data JPA, Hibernate |
| API Documentation | Swagger / Springdoc OpenAPI |
| Testing | JUnit 5, Mockito |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Build Tool | Maven |

---

## Architecture

```text
Client / Swagger UI
        |
        v
Spring Boot REST API
        |
        |-- Auth Module
        |     |-- Register
        |     |-- Login
        |     |-- JWT generation and validation
        |
        |-- URL Module
        |     |-- Create short URL
        |     |-- Custom alias
        |     |-- Redirect
        |     |-- Soft delete
        |
        |-- Analytics Module
        |     |-- Track clicks
        |     |-- Store click metadata
        |
        |-- Redis
        |     |-- Cache short URL redirects
        |     |-- Rate limiting
        |
        |-- PostgreSQL
              |-- Users
              |-- URLs
              |-- Click events
```

---

## Project Structure

```text
scalable-url-shortener/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/abhishek/urlshortener/
│   │   │   │   ├── analytics/
│   │   │   │   ├── auth/
│   │   │   │   ├── config/
│   │   │   │   ├── exception/
│   │   │   │   ├── url/
│   │   │   │   ├── user/
│   │   │   │   └── UrlshortenerApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       ├── java/com/abhishek/urlshortener/
│   │       └── resources/
│   │           └── application.properties
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
├── .github/
│   └── workflows/
│       └── backend-ci.yml
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## API Overview

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT token |

### URLs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/urls` | Create a shortened URL |
| GET | `/api/urls/my` | Get current user's URLs |
| DELETE | `/api/urls/{id}` | Deactivate a URL |
| GET | `/{shortCode}` | Redirect to the original URL |

### Analytics

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/analytics/{shortCode}` | Get click analytics for a short URL |

### Health

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Service health check |

---

## Swagger Documentation

After starting the application, open:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Run with Docker Compose

From the project root:

```bash
docker compose up -d --build
```

This starts:

- Spring Boot backend
- PostgreSQL
- Redis

Check running containers:

```bash
docker ps
```

Expected containers:

```text
urlshortener-backend
urlshortener-postgres
urlshortener-redis
```

Test the health endpoint:

```bash
curl http://localhost:8080/api/health
```

Expected:

```text
OK
```

Stop containers:

```bash
docker compose down
```

Stop containers and delete database volume:

```bash
docker compose down -v
```

Use `-v` only when you intentionally want to delete all database data.

---

## Run Locally Without Docker Backend

Start PostgreSQL and Redis:

```bash
docker compose up -d postgres redis
```

Run the backend locally:

```bash
cd backend
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

---

## Run Tests

Make sure PostgreSQL and Redis are running:

```bash
docker compose up -d postgres redis
```

Run tests:

```bash
cd backend
./mvnw test
```

On Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd test
```

Expected result:

```text
BUILD SUCCESS
```

---

## Example API Usage

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Abhishek Bala",
    "email": "abhishek@example.com",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "abhishek@example.com",
    "password": "password123"
  }'
```

### Create Short URL

```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "originalUrl": "https://www.github.com",
    "customAlias": "github-link"
  }'
```

### Redirect

Open in browser:

```text
http://localhost:8080/github-link
```

### Get My URLs

```bash
curl -X GET http://localhost:8080/api/urls/my \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Analytics

```bash
curl -X GET http://localhost:8080/api/analytics/github-link \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## GitHub Actions CI

The repository includes a GitHub Actions workflow:

```text
.github/workflows/backend-ci.yml
```

The CI pipeline runs automatically on push and pull requests.

It performs:

- Checkout
- Java 17 setup
- PostgreSQL service startup
- Redis service startup
- Maven tests
- Spring Boot JAR build
- Docker image validation

---

## Environment Variables

Use `.env.example` as a reference for local environment configuration.

Important variables:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
SPRING_DATA_REDIS_HOST
SPRING_DATA_REDIS_PORT
APP_JWT_SECRET
APP_JWT_EXPIRATION_MS
APP_RATE_LIMIT_URL_CREATION_PER_HOUR
APP_RATE_LIMIT_REDIRECTS_PER_MINUTE
```

For production, use a secure JWT secret and do not commit real secrets to GitHub.

---

## Testing Summary

The backend includes unit and application-context tests covering:

- User registration
- User login
- Duplicate email validation
- Invalid login handling
- URL creation
- Custom alias conflicts
- Invalid URL validation
- URL listing
- Redis cache hit
- Redis cache miss
- Redirect resolution
- Expired URL handling
- URL deactivation
- Spring Boot application startup

---

## Author

Abhishek Bala

GitHub: [Electrolight123](https://github.com/Electrolight123)
