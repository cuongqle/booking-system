# Hold — Booking System

Hold is a full-stack generic resource booking app. Authenticated users pick a resource (meeting room, desk, equipment, and more), book a time window, track reservations on a calendar, and update booking status. Overlapping **Pending** or **Confirmed** bookings on the same resource are rejected.

**Demo:** [https://booking-system-neon-eta.vercel.app/](https://booking-system-neon-eta.vercel.app/)

## Features

- Multi-tenant organizations: users belong to a company; resources/bookings/invoices are scoped by org
- Roles: `USER`, org `ADMIN`, and platform `SUPER_ADMIN` (manage all orgs; not tied to a tenant)
- Register / sign in with JWT (`USER` when joining; create-org makers become org `ADMIN`)
- Platform org list/create/suspend for `SUPER_ADMIN` (suspended orgs cannot log in or use APIs)
- Platform user management per org: promote/demote `ADMIN`, activate/deactivate accounts
- Persisted resource catalog with types (`MEETING_ROOM`, `DESK`, `EQUIPMENT`, `OTHER`)
- Admin resource create/edit (active/inactive) within their organization
- Create, view, and edit bookings with start/end date-time
- Hourly pricing with total amount snapshotted on each booking
- Stay rules per resource: min/max duration and turnover buffer
- Operating hours and blackout periods per resource
- Status workflow: Pending → Confirmed (after payment) → Canceled / Completed
- Stub invoices: create booking → unpaid invoice; pay confirms booking; PDF download
- Users can list their bookings/invoices; org admins can list all within the org with filters
- Conflict validation on create and update (includes buffer windows)
- Month calendar view of your reservations
- In-app notifications for booking create/update/payment (header bell)
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
| `POST` | `/auth/register` | Public — `organizationName` (create org as ADMIN) **or** `organizationSlug` (join as USER) |
| `POST` | `/auth/login` | Public → JWT (includes `organizationId` / name / slug) |
| `GET` | `/resources` | Active resources for caller’s org |
| `GET` | `/admin/resources` | All org resources (**ADMIN**) |
| `POST` | `/admin/resources` | Create resource (**ADMIN**) |
| `PUT` | `/admin/resources/{id}` | Update resource (**ADMIN**; includes `openTime`/`closeTime`) |
| `GET` | `/admin/resources/{id}/blackouts` | List blackouts (**ADMIN**) |
| `POST` | `/admin/resources/{id}/blackouts` | Create blackout (**ADMIN**) |
| `DELETE` | `/admin/resources/{id}/blackouts/{blackoutId}` | Delete blackout (**ADMIN**) |
| `GET` | `/bookings` | Current user’s bookings (`?status=&resourceId=`) |
| `POST` | `/bookings` | Create (`PENDING` + unpaid invoice); enforces hours/blackouts |
| `GET` | `/bookings/{id}` | Own booking (org admins can open any in org) |
| `PUT` | `/bookings/{id}` | Update resource, times, status (`CONFIRMED` requires paid invoice) |
| `GET` | `/bookings/{id}/invoice` | Invoice for booking |
| `GET` | `/bookings/{id}/invoice/pdf` | Download invoice PDF |
| `POST` | `/bookings/{id}/pay` | Stub payment → invoice `PAID`, booking `CONFIRMED` |
| `GET` | `/invoices` | Current user’s invoices (`?status=`) |
| `GET` | `/admin/bookings` | Org bookings (**ADMIN**; `?status=&resourceId=&userId=`) |
| `GET` | `/admin/invoices` | Org invoices (**ADMIN**; `?status=&userId=&bookingId=`) |
| `GET` | `/platform/organizations` | List all orgs (**SUPER_ADMIN**) |
| `GET` | `/platform/organizations/{id}` | Org detail + user count (**SUPER_ADMIN**) |
| `POST` | `/platform/organizations` | Create org (**SUPER_ADMIN**; `{ "name" }`) |
| `POST` | `/platform/organizations/{id}/suspend` | Suspend org (**SUPER_ADMIN**; optional `{ "reason" }`) |
| `POST` | `/platform/organizations/{id}/unsuspend` | Restore org (**SUPER_ADMIN**) |
| `GET` | `/platform/organizations/{id}/users` | List org users (**SUPER_ADMIN**) |
| `PATCH` | `/platform/users/{id}` | Update role (`USER`/`ADMIN`) and/or `active` (**SUPER_ADMIN**) |
| `GET` | `/notifications` | Current user’s notifications |
| `GET` | `/notifications/unread-count` | Unread badge count |
| `POST` | `/notifications/{id}/read` | Mark one read |
| `POST` | `/notifications/read-all` | Mark all read |

Seeded demo org (`V11` → `V12`): slug **`hold`**, name **Hold Demo**. Seed admin (`V6`) belongs to it:

| Field | Value |
| --- | --- |
| Email | `admin@hold.com` |
| Password | `Admin123!` |
| Role | `ADMIN` |
| Org slug | `hold` |

Seeded platform super admin (`V13`) — no org; manages all tenants:

| Field | Value |
| --- | --- |
| Email | `superadmin@hold.com` |
| Password | `SuperAdmin123!` |
| Role | `SUPER_ADMIN` |

Promote an admin within an org (after register/login once):

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'you@example.com';
```

Then sign out and sign in again so the JWT includes `ADMIN`.

Health check (no `/api/v1` prefix): `GET /health`
