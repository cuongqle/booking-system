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
# Postgres + backend (API on http://localhost:8080)
docker compose up --build

# Or run pieces separately:
docker compose up -d postgres
cd backend && ./mvnw spring-boot:run

# Frontend
cd frontend && npm start   # or yarn start → http://localhost:4200
```

Datasource and JWT settings can be overridden with env vars (`SPRING_DATASOURCE_*`, `APP_JWT_*`). In Compose, the API talks to Postgres via hostname `postgres`.

## Deploy frontend (Vercel)

1. In the Vercel project, set **Root Directory** to `frontend`.
2. Use defaults (or): Build `npm run build`, Output `dist/frontend/browser`.
3. Clear any custom Install Command that still says `npm install --prefix frontend` (that doubles the path when Root Directory is already `frontend`).

`frontend/vercel.json` sets the output directory and SPA rewrites to `index.html`.

Note: production `apiBaseUrl` is empty, so the UI calls `/api/v1` on the same host. Point it at your deployed backend (or add a Vercel rewrite/proxy) before relying on auth/bookings in production.
