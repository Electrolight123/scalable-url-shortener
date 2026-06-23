# Scalable URL Shortener

A full-stack scalable URL shortener application built with React, TypeScript, Spring Boot, PostgreSQL, Redis, JWT authentication, Swagger API documentation, Docker, and GitHub Actions CI.

The application allows users to register, login, create short URLs, use custom aliases, manage their URLs, redirect through short links, and view click analytics.

---

## Features

### Frontend

- React + TypeScript frontend
- Register and login pages
- JWT token storage
- Protected dashboard routes
- Create short URL form
- Custom alias support
- My URLs dashboard
- Copy short URL button
- Open short URL button
- Delete/deactivate URL
- Analytics page
- Clean responsive UI

### Backend

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
- Dockerized backend
- Unit tests with JUnit and Mockito

### DevOps

- Dockerized frontend
- Dockerized backend
- PostgreSQL and Redis through Docker Compose
- Full-stack Docker Compose setup
- GitHub Actions CI pipeline
- Backend test validation
- Frontend build validation
- Backend Docker image validation
- Frontend Docker image validation

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React, Vite, TypeScript |
| Frontend API Client | Axios |
| Frontend Routing | React Router DOM |
| Backend | Java 17, Spring Boot |
| Database | PostgreSQL 16 |
| Cache | Redis 7 |
| Authentication | JWT |
| ORM | Spring Data JPA, Hibernate |
| API Documentation | Swagger / Springdoc OpenAPI |
| Testing | JUnit 5, Mockito |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Build Tools | Maven, npm |

---

## Architecture

```text
React Frontend
localhost:5173
        |
        | HTTP / Axios
        v
Spring Boot REST API
localhost:8080
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
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   └── api.ts
│   │   ├── components/
│   │   │   ├── Navbar.tsx
│   │   │   └── ProtectedRoute.tsx
│   │   ├── pages/
│   │   │   ├── Analytics.tsx
│   │   │   ├── CreateUrl.tsx
│   │   │   ├── Dashboard.tsx
│   │   │   ├── Login.tsx
│   │   │   └── Register.tsx
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   └── index.css
│   ├── Dockerfile
│   ├── package.json
│   └── package-lock.json
│
├── .github/
│   └── workflows/
│       └── backend-ci.yml
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## Application URLs

After starting the project:

| Service | URL |
|---|---|
| Frontend App | `http://localhost:5173` |
| Backend API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Health Check | `http://localhost:8080/api/health` |

---

## Run Full Stack with Docker Compose

From the project root:

```bash
docker compose up -d --build
```

This starts:

- React frontend
- Spring Boot backend
- PostgreSQL
- Redis

Check running containers:

```bash
docker ps
```

Expected containers:

```text
urlshortener-frontend
urlshortener-backend
urlshortener-postgres
urlshortener-redis
```

Test backend health:

```bash
curl http://localhost:8080/api/health
```

Expected:

```text
OK
```

Open the frontend:

```text
http://localhost:5173
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

## Run Frontend Locally

From the frontend folder:

```bash
cd frontend
npm install
npm run dev
```

Frontend will run at:

```text
http://localhost:5173
```

---

## Run Backend Locally

Start PostgreSQL and Redis:

```bash
docker compose up -d postgres redis
```

Run backend:

```bash
cd backend
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Backend will run at:

```text
http://localhost:8080
```

---

## Run Tests

Make sure PostgreSQL and Redis are running:

```bash
docker compose up -d postgres redis
```

Run backend tests:

```bash
cd backend
./mvnw test
```

On Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd test
```

Run frontend build:

```bash
cd frontend
npm install
npm run build
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
    "originalUrl": "https://github.com/Electrolight123",
    "customAlias": "github-profile"
  }'
```

### Redirect

Open in browser:

```text
http://localhost:8080/github-profile
```

### Get My URLs

```bash
curl -X GET http://localhost:8080/api/urls/my \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Analytics

```bash
curl -X GET http://localhost:8080/api/analytics/github-profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Swagger Documentation

After starting the backend, open:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## GitHub Actions CI

The repository includes a GitHub Actions workflow:

```text
.github/workflows/backend-ci.yml
```

The CI pipeline runs automatically on push and pull requests.

It validates:

- Backend tests
- Backend JAR build
- Frontend TypeScript build
- Backend Docker image build
- Frontend Docker image build

---

## Environment Variables

Use `.env.example` as a reference for local environment configuration.

Important backend variables:

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

Important frontend variable:

```text
VITE_API_BASE_URL
```

For production, use a secure JWT secret and do not commit real secrets to GitHub.

---

## Testing Summary

The backend includes tests covering:

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

The frontend build validates:

- TypeScript correctness
- React production build
- Vite build pipeline

---

## Author

Abhishek Bala

GitHub: [Electrolight123](https://github.com/Electrolight123)
