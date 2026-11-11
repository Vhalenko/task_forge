# TaskForge

TaskForge is a full-stack task management application for teams. It provides authentication, role-based access control, projects, tasks, comments and team administration through a Spring Boot REST API and a React frontend.

The repository is organized as a monorepo and can be run locally with Docker Compose.

## Highlights

- JWT-based authentication with BCrypt password hashing
- Role-based authorization for administrative operations
- Teams, projects, tasks, assignees and comments
- Validation and consistent REST error responses
- PostgreSQL persistence with JPA/Hibernate
- Redis-backed caching for selected read operations
- Request correlation IDs through SLF4J MDC
- Dockerized backend, frontend, PostgreSQL and Redis services
- Automated backend and frontend CI pipelines
- Unit and integration tests with an H2 test database
- Postman request collection for API testing

## Technology stack

### Backend

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Spring Data Redis / Redis Cache
- PostgreSQL
- Maven
- JUnit, Spring Boot Test and MockMvc

### Frontend

- React
- TypeScript
- Vite
- Axios
- React Router
- Tailwind CSS
- Nginx

### Infrastructure

- Docker and Docker Compose
- GitHub Actions

## Architecture

```text
Browser
   |
   v
Nginx / React frontend :3000
   |
   | /api/* proxy
   v
Spring Boot backend :8080
   |                  |
   v                  v
PostgreSQL :5432    Redis :6379
```

The frontend Nginx container proxies `/api/` requests to the backend service. Containers communicate through Docker Compose service names, so the backend connects to PostgreSQL using `database` and to Redis using `redis`.

## Prerequisites

- Docker Desktop with Docker Compose
- Java 21 and Maven only if running the backend outside Docker
- Node.js 22 and npm only if running the frontend outside Docker

## Configuration

Create a root `.env` file next to `docker-compose.yml`:

```dotenv
DB_USERNAME=postgres
DB_PASSWORD=change-me
JWT_SECRET=base64-encoded-secret

# Optional. Keep these only if Loki integration is enabled.
GRAFANA_LOKI_URL=https://your-loki-endpoint/loki/api/v1/push
GRAFANA_LOKI_USER=your-loki-user
GRAFANA_LOKI_TOKEN=your-loki-token
```

Never commit `.env` or real credentials. Use `.env.example` for shareable configuration documentation.

For a backend started from the IDE, Redis is normally available at `localhost:6379`. For a backend running inside Docker Compose, use:

```properties
spring.data.redis.host=redis
spring.data.redis.port=6379
```

## Run with Docker Compose

From the repository root:

```bash
docker compose up -d
```

The application is available at:

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- PostgreSQL: localhost:5432
- Redis: localhost:6379

Useful commands:

```bash
docker compose ps
docker compose logs -f backend
docker compose logs -f frontend
docker compose down
```

To stop the containers without deleting database data, use `docker compose down`. Do not use `docker compose down -v` unless you intentionally want to remove the named volumes.

## Run the backend locally

Start PostgreSQL and Redis first, then run from `backend`:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

For local execution, configure the datasource and Redis host for `localhost` through environment variables or a local Spring profile.

## Run the frontend locally

```bash
cd frontend
npm ci
npm run dev
```

The Vite development server normally runs at http://localhost:5173. The production Docker setup serves the built application through Nginx on port 3000.

## Testing and quality checks

Run backend tests:

```bash
cd backend
./mvnw test
```

On Windows:

```powershell
cd backend
.\mvnw.cmd test
```

The `test` profile uses an in-memory H2 database and disables Redis caching so tests do not depend on external infrastructure.

Run the frontend checks:

```bash
cd frontend
npm ci
npm run lint
npm run build
```

GitHub Actions runs backend tests, backend packaging and frontend checks for repository changes.

## API overview

The API is versioned under `/api/v1`.

| Area | Main endpoints |
| --- | --- |
| Authentication | `POST /api/v1/auth/register`, `POST /api/v1/auth/login` |
| Teams | `GET/POST /api/v1/teams`, `GET /api/v1/teams/{id}` |
| Projects | `GET /api/v1/projects/{id}`, project task endpoints |
| Tasks | `GET/PUT/DELETE /api/v1/tasks/{id}` |
| Comments | `POST /api/v1/tasks/{id}/comments`, `PUT/DELETE /api/v1/comments/{id}` |
| Users | `GET /api/v1/users/{id}`, profile and task endpoints |

Authentication uses a bearer token:

```http
Authorization: Bearer <jwt-token>
```

The complete request collection is available under `backend/postman/`.

## Project structure

```text
.
├── backend/
│   ├── src/main/java/com/taskforge/
│   │   ├── controller/       REST endpoints
│   │   ├── service/          application and authorization logic
│   │   ├── repository/       persistence access
│   │   ├── model/            entities, DTOs and requests
│   │   ├── security/         JWT and Spring Security components
│   │   ├── exception/        API error handling
│   │   └── config/           application configuration
│   ├── src/test/             backend tests
│   └── postman/              API request collection
├── frontend/                 React application
├── .github/workflows/        CI/CD workflows
├── docker-compose.yml        local multi-container environment
└── .env                      local secrets, not committed
```

## Security notes

- Secrets are supplied through environment variables and are not stored in source control.
- Passwords are stored as BCrypt hashes, never as plaintext.
- JWT secrets should be long, random and different per environment.
- Production deployments should use HTTPS and restrict CORS origins to the real frontend domain.
- Do not expose PostgreSQL or Redis publicly in production unless access is protected by a private network and authentication.

## Current portfolio scope

The project demonstrates a complete junior-level backend workflow: layered architecture, DTO mapping, validation, authorization, persistence, caching, automated tests, Docker and CI. Possible next improvements include OpenAPI documentation, database migrations with Flyway, Testcontainers for PostgreSQL/Redis integration tests, structured JSON logging and a deployment environment with health checks and monitoring.

## License

This project is currently intended as a portfolio project. Add a license if you plan to distribute or reuse the code publicly.
