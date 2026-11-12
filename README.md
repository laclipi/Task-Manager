# Task Manager

A full-stack task management application built as a junior full-stack portfolio project. Users can register, log in with JWT-based authentication, and manage their own private list of tasks through a REST API consumed by a React single-page application.

> **Status:** work in progress. The backend REST API is functional; the frontend currently covers login, listing tasks and creating tasks. See [Current Status](#current-status) for details.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Main Features](#main-features)
- [Tech Stack](#tech-stack)
- [Architecture and Project Structure](#architecture-and-project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Database Configuration](#database-configuration)
- [REST API Endpoints](#rest-api-endpoints)
- [Running the Tests](#running-the-tests)
- [Current Status](#current-status)
- [Future Improvements](#future-improvements)

---

## Project Overview

The goal of this project is to build a complete, end-to-end web application that demonstrates the core pieces of a typical full-stack workflow:

- A **Spring Boot** REST API with stateless **JWT** authentication and Spring Security.
- A **React + TypeScript** frontend that authenticates against the API and talks to protected endpoints.
- Per-user data isolation: every task belongs to an owner, and users can only read, modify or delete their own tasks.

## Main Features

**Implemented in the backend**

- **User registration** with email and password validation (valid email, password of at least 6 characters). Passwords are hashed with BCrypt; duplicate emails are rejected with `409 Conflict`.
- **JWT authentication**: logging in returns a signed token (HS256) valid for 1 hour. A custom filter validates the `Authorization: Bearer <token>` header on every protected request.
- **Task management (CRUD)**: create, list, retrieve, update and delete tasks. Tasks are scoped to the authenticated user, so accessing another user's task returns `404 Not Found`.
- **Request validation** with Bean Validation (`@Valid`) on registration, login and task payloads.
- **Centralised error handling** for duplicate emails (`409`) and invalid credentials (`401`).
- **CORS** configured for the frontend development origin (`http://localhost:5173`).

**Implemented in the frontend**

- **Login page** that calls `POST /auth/login`, stores the token in `localStorage` and redirects to the tasks page.
- **Tasks page** that lists the authenticated user's tasks and lets them create new ones.
- Client-side routing with React Router (`/login`, `/tasks`; any unknown route redirects to `/login`).

## Tech Stack

### Backend

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Web | Spring Web MVC |
| Security | Spring Security, JWT with [jjwt](https://github.com/jwtk/jjwt) 0.13.0, BCrypt |
| Persistence | Spring Data JPA (Hibernate), H2 database |
| Validation | Spring Boot Validation (Bean Validation) |
| Utilities | Lombok |
| Build tool | Maven (Maven Wrapper included) |
| Testing | JUnit 5, Mockito, Spring Boot Test |

### Frontend

| Area | Technology |
| --- | --- |
| Library | React 19 |
| Language | TypeScript |
| Build tool | Vite |
| Routing | React Router (`react-router-dom` 7) |
| Linting | ESLint (with `typescript-eslint`) |

## Architecture and Project Structure

The project is a monorepo with two independent applications. The React frontend communicates with the Spring Boot backend over HTTP/JSON, and the backend follows a layered architecture (controller → service → repository).

```text
┌───────────────────────┐   HTTP + JSON (JWT)   ┌─────────────────────────────┐
│  React + Vite SPA     │ ────────────────────► │  Spring Boot REST API       │
│  http://localhost:5173│                       │  http://localhost:8080      │
└───────────────────────┘                       │  Security → Controller →    │
                                                │  Service → Repository → H2  │
                                                └─────────────────────────────┘
```

```text
Task-Manager/
├── backend/                         # Spring Boot application
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd              # Maven Wrapper
│   └── src/
│       ├── main/
│       │   ├── java/com/laura/taskmanager/
│       │   │   ├── config/          # SecurityConfig, CorsConfig
│       │   │   ├── controller/      # AuthController, TaskController
│       │   │   ├── dto/             # Request/response objects
│       │   │   ├── exception/       # Global exception handling
│       │   │   ├── model/           # JPA entities: User, Task
│       │   │   ├── repository/      # Spring Data JPA repositories
│       │   │   ├── security/        # JwtService, JwtAuthFilter, UserDetailsService
│       │   │   └── service/         # AuthService, TaskService
│       │   └── resources/
│       │       └── application.properties
│       └── test/                    # TaskServiceTest, TaskmanagerApplicationTests
├── frontend/                        # React + TypeScript application
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── App.tsx                  # Routes
│       ├── pages/                   # LoginPage, TasksPage
│       └── services/                # authService, taskService (API calls)
└── README.md
```

## Prerequisites

- **JDK 21** (the project is configured with `java.version` 21). The `JAVA_HOME` environment variable must point to a JDK 21 installation, because the Maven Wrapper uses it to locate Java. Check it with `echo $JAVA_HOME` (macOS / Linux) or `echo $env:JAVA_HOME` (PowerShell). Note that `java -version` only reports the `java` found first on your `PATH`, which may be a different version.
- **Node.js** `^20.19.0 || >=22.12.0` (required by Vite 8) and **npm**.
- **Git**.
- Maven does **not** need to be installed separately; the included Maven Wrapper downloads it on first use.

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd Task-Manager
```

### 2. Run the backend

```bash
cd backend
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS / Linux:

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080` (Spring Boot default port).

### 3. Run the frontend

In a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Vite serves the app at `http://localhost:5173`. Open it in the browser, log in, and you will be redirected to the tasks page.

> The frontend calls the API at the fixed URL `http://localhost:8080`, and the backend only allows CORS requests from `http://localhost:5173`. Keep both on their default ports.

### Other frontend scripts

```bash
npm run build     # Type-check (tsc -b) and build for production
npm run lint      # Run ESLint
npm run preview   # Preview the production build
```

## Database Configuration

The project uses an **H2 in-memory database**, so no database installation or setup is needed.

- `application.properties` does not define a datasource, so Spring Boot auto-configures an embedded H2 database.
- The schema is generated automatically by Hibernate from the JPA entities (`users` and `task` tables). Spring Boot's default behaviour for an embedded database is to create the schema on startup and drop it on shutdown.
- **Data is not persisted between restarts.** Every time the backend starts you begin with an empty database, so you need to register a user again.

Current entities:

| Entity | Table | Fields |
| --- | --- | --- |
| `User` | `users` | `id`, `email` (unique, not null), `passwordHash` |
| `Task` | `task` | `id`, `title`, `owner_id` (many-to-one to `users`) |

The H2 console dependency is included in the `pom.xml`, but the console is not explicitly enabled in `application.properties`.

### Application properties

The only custom property is the secret used to sign JWTs:

```properties
spring.application.name=taskmanager
jwt.secret=mi-clave-secreta-local-para-desarrollo
```

This value is intended for **local development only**. Do not reuse it in any real deployment.

## REST API Endpoints

Base URL: `http://localhost:8080`

### Authentication (public)

| Method | Endpoint | Description | Success response |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | Register a new user | `201 Created` with `{ id, email }` |
| `POST` | `/auth/login` | Authenticate and obtain a JWT | `200 OK` with the token as plain text |

Request body for both endpoints:

```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

Possible errors: `400` (validation), `409` (email already registered, on register), `401` (invalid credentials, on login).

### Tasks (require `Authorization: Bearer <token>`)

| Method | Endpoint | Description | Success response |
| --- | --- | --- | --- |
| `GET` | `/tasks` | List the authenticated user's tasks | `200 OK` with `[{ id, title }]` |
| `GET` | `/tasks/{id}` | Get one of the user's tasks | `200 OK` with `{ id, title }` |
| `POST` | `/tasks` | Create a task | `201 Created` with `{ id, title }` |
| `PUT` | `/tasks/{id}` | Update a task's title | `200 OK` with `{ id, title }` |
| `DELETE` | `/tasks/{id}` | Delete a task | `204 No Content` |

Request body for `POST /tasks` and `PUT /tasks/{id}`:

```json
{
  "title": "Study JWT"
}
```

Possible errors: `400` (blank title), `401` (missing or invalid token), `404` (task not found or owned by another user).

### Example usage

```bash
# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret123"}'

# Log in (returns the JWT)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret123"}'

# Create a task using the token returned by the login call
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title":"Study JWT"}'
```

> In Windows PowerShell, use `curl.exe` instead of `curl` (which is an alias for `Invoke-WebRequest`) and adjust the quoting of the JSON body.

## Running the Tests

Backend tests are run with the Maven Wrapper from the `backend` directory:

```bash
cd backend
```

On Windows:

```powershell
.\mvnw.cmd test
```

On macOS / Linux:

```bash
./mvnw test
```

Existing tests:

- `TaskServiceTest`: unit test (JUnit 5 + Mockito) that verifies task creation in `TaskService`.
- `TaskmanagerApplicationTests`: Spring Boot test that checks the application context loads.

The frontend does not have automated tests yet. You can run the linter with `npm run lint` from the `frontend` directory.

## Current Status

**Implemented**

- User registration, login and JWT-protected endpoints.
- Full task CRUD in the backend API, with per-user ownership checks.
- Frontend login flow and task listing/creation against the real API.

**Known limitations**

- The frontend does not have a registration screen yet; new users must be created through the API (`POST /auth/register`).
- The frontend does not yet expose updating or deleting tasks, although the backend supports both.
- There is no logout action and no route guard on `/tasks` in the frontend; the JWT is stored in `localStorage`.
- Tasks only have a `title` (no description, status or due date).
- Test coverage is minimal: one service unit test and a context-load test. There are no frontend tests.
- The database is in-memory, so data is lost on every restart.
- The JWT secret is stored in `application.properties` and the API/CORS URLs are hard-coded for local development.

## Future Improvements

These are ideas for future work and are **not** implemented yet:

- Registration screen and logout in the frontend, plus a protected route for `/tasks`.
- Edit and delete task actions in the UI.
- Richer task model (description, completion status, due date).
- Persistent database (for example PostgreSQL or MySQL) and externalised configuration for secrets and URLs via environment variables.
- More tests: controller/security integration tests for the backend and component tests for the frontend.
- Consistent JSON error responses and better UX feedback for errors.
- Containerisation and a CI pipeline.
