# Harbor — Booking System

A small full-stack learning app for reserving rooms. Users register/sign in with JWT, pick a room from a fixed catalog, book a time window, and manage status (Pending, Confirmed, Canceled, Completed). Overlapping Pending/Confirmed bookings on the same room are rejected.

## Tech stack

### Backend (`backend/`)

| Technology | Version |
| --- | --- |
| Java | 21 |
| Spring Boot | 4.1.1 |
| Spring Web MVC, Security, Validation, Data JPA | (via Boot) |
| Flyway | (via Boot) |
| PostgreSQL | 16 (Docker: `postgres:16-alpine`) |
| JJWT | 0.12.6 |
| H2 | (tests only) |
| Maven | Wrapper included |

API base path: `/api/v1`

### Frontend (`frontend/`)

| Technology | Version |
| --- | --- |
| Angular | 21.2 |
| TypeScript | ~5.9 |
| RxJS | ~7.8 |
| Tailwind CSS | 4.1 |
| Vitest | ^4.0 (unit tests) |

Dev server proxies API calls to the backend (default `localhost:8080`).

## Quick start

```bash
# Database
docker compose up -d

# Backend (Java 21)
cd backend && ./mvnw spring-boot:run

# Frontend
cd frontend && npm start   # or yarn start → http://localhost:4200
```
