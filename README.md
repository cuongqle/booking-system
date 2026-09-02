# Harbor — Booking System

Harbor is a full-stack room reservation app. Authenticated users pick a room from the catalog, book a time window, track stays on a calendar, and update booking status. Overlapping **Pending** or **Confirmed** bookings on the same room are rejected.

## Features

- Register / sign in with JWT
- Room catalog selection (no free-text room IDs)
- Create, view, and edit bookings with start/end date-time
- Status workflow: Pending → Confirmed → Canceled / Completed
- Conflict validation on create and update
- Month calendar view of your reservations
- Field-level form validation on auth and booking screens

## Project structure

```text
booking-system/
├── backend/          Spring Boot API (`/api/v1`)
├── frontend/         Angular SPA
└── docker-compose.yml
```

## Tech stack

### Backend (`backend/`)

| Technology | Version |
| --- | --- |
| Java | 21 |
| Spring Boot | 4.1.1 |
| Spring Web MVC, Security, Validation, Data JPA | via Boot |
| Flyway | via Boot |
| PostgreSQL | 16 (`postgres:16-alpine`) |
| JJWT | 0.12.6 |
| H2 | tests only |
| Maven Wrapper | included |

### Frontend (`frontend/`)

| Technology | Version |
| --- | --- |
| Angular | 21.2 |
| TypeScript | ~5.9 |
| RxJS | ~7.8 |
| Tailwind CSS | 4.1 |
| Vitest | ^4.0 (unit tests via `ng test`) |

Local `ng serve` proxies API traffic to `http://localhost:8080`.

## Quick start

**Requirements:** Docker, Java 21 (if running the API outside Compose), Node.js 20+.

### Full stack with Docker

```bash
docker compose up --build
```

- API: [http://localhost:8080](http://localhost:8080)
- Postgres: `localhost:5432` (`booking_system` / `postgres` / `postgres`)

### Frontend (local)

```bash
cd frontend
npm install
npm start
```

App: [http://localhost:4200](http://localhost:4200)

### Backend without Compose (local JVM)

```bash
docker compose up -d postgres
cd backend
./mvnw spring-boot:run
```

## Configuration

Backend settings use env vars with local defaults in `application.properties`:

| Variable | Purpose |
| --- | --- |
| `SPRING_DATASOURCE_URL` | JDBC URL (Compose uses host `postgres`) |
| `SPRING_DATASOURCE_USERNAME` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `APP_JWT_SECRET` | JWT signing secret (≥ 32 chars) |
| `APP_JWT_EXPIRATION_MS` | Token lifetime (ms) |
| `APP_CORS_ALLOWED_ORIGINS` | Comma-separated browser origins |

Example Neon JDBC URL:

```text
jdbc:postgresql://<host>/<db>?sslmode=require
```

Frontend production API host is set in `frontend/src/environments/environment.ts` (`apiBaseUrl`). Development uses `environment.development.ts` (defaults to `http://localhost:8080`).

## Deploy

### Frontend (Vercel)

1. Set **Root Directory** to `frontend`.
2. Build: `npm run build` · Output: `dist/frontend/browser`.
3. Do not use `npm install --prefix frontend` when Root Directory is already `frontend`.

`frontend/vercel.json` configures the output directory and SPA rewrites to `index.html`.

Ensure `apiBaseUrl` points at your deployed API and that the API CORS list includes the Vercel origin (no trailing slash).

### Backend

Run the Spring Boot app on any Java 21 host (Render, Railway, Fly.io, VM, etc.) with Postgres (Docker or Neon). Set the datasource, JWT, and CORS env vars above. Flyway migrations apply on startup.

## API overview

Base path: `/api/v1`

| Method | Path | Notes |
| --- | --- | --- |
| `POST` | `/auth/register` | Public |
| `POST` | `/auth/login` | Public → JWT |
| `GET` | `/rooms` | Auth required |
| `GET` | `/bookings` | Current user’s bookings |
| `POST` | `/bookings` | Create (`PENDING`) |
| `GET` | `/bookings/{id}` | Own booking |
| `PUT` | `/bookings/{id}` | Update room, times, status |

Health check (no `/api/v1` prefix): `GET /health`
