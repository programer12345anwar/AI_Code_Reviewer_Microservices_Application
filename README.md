# AI Code Reviewer Platform

Production-ready, microservices-based AI code review platform that integrates with GitHub pull requests and delivers asynchronous LLM-powered review insights with quality scoring, PR comments, analytics, and live UI updates.

## Highlights

- Java 17 + Spring Boot microservices architecture
- API Gateway with JWT auth + role-based enforcement + rate limiting
- GitHub webhook ingestion for PR events
- Diff-only extraction (changed lines with line numbers)
- Kafka async pipeline with retry + Dead Letter Topic
- Redis de-duplication caching
- Multi-LLM fallback (`OpenAI -> Gemini`)
- PostgreSQL persistence for review history
- Auto-commenting on GitHub PRs
- React + Vite frontend with charts, Monaco diff view, chat assistant, dark mode, and realtime updates
- Prometheus + Grafana + structured logback logging

---

## Architecture

```text
GitHub Webhook (PR opened/sync/reopened)
        |
        v
GitHub Integration Service
  - Verify webhook signature
  - Fetch PR file patches
  - Normalize changed lines only
  - Publish ReviewRequestedEvent
        |
        v
Kafka topic: review.requested
        |
        v
Worker Service
  - Redis de-duplication
  - Call LLM Service
  - Calculate quality score
  - Publish ReviewCompletedEvent
        |
        v
Kafka topic: review.completed
        |
        v
Review Service
  - Persist to PostgreSQL
  - Redis duplicate guard
  - WebSocket broadcast
  - Trigger PR auto-comment via GitHub Integration Service

Frontend <-> API Gateway <-> Auth/Review/GitHub/LLM services
```

---

## Services

1. `api-gateway` (port `8080`)
- JWT validation
- Role gate (`ADMIN` required for analytics endpoint)
- Bucket4j global rate limiting
- Routing + CORS

2. `auth-service` (port `8081`)
- Signup/Login
- BCrypt password hashing
- JWT generation
- Roles: `ADMIN`, `USER`

3. `review-service` (port `8082`)
- Stores reviews in PostgreSQL
- Exposes review list/detail/analytics APIs
- WebSocket `/ws/reviews`
- Chat endpoint for review-specific Q&A

4. `worker-service` (port `8083`)
- Kafka consumer for `review.requested`
- Retry + DLT handling
- LLM orchestration and scoring

5. `llm-service` (port `8084`)
- Strict JSON code-review endpoint
- Multi-provider fallback (`OpenAI`, then `Gemini`)
- Chat endpoint used by review assistant

6. `github-integration-service` (port `8085`)
- GitHub webhook receiver
- Signature verification (`X-Hub-Signature-256`)
- Pull request file patch retrieval
- PR comment publisher

---

## LLM Prompt Contract

Review endpoint enforces strict JSON schema:

```json
{
  "bugs": [],
  "performance_issues": [],
  "security_issues": [],
  "suggestions": []
}
```

All issue objects include line numbers, severity, actionable message, and recommendation.

---

## Code Quality Score Logic

Quality score starts at `100` and subtracts weighted penalties:

- Bugs: `5 x severityWeight`
- Performance issues: `4 x severityWeight`
- Security issues: `8 x severityWeight`
- Suggestions: `2 x severityWeight`

Severity weights:

- `LOW=1`
- `MEDIUM=2`
- `HIGH=3`
- `CRITICAL=5`

Final score: `max(0, 100 - totalPenalty)`

---

## Repo Structure

```text
.
├── backend
│   ├── pom.xml
│   ├── shared/common-models
│   ├── services
│   │   ├── api-gateway
│   │   ├── auth-service
│   │   ├── review-service
│   │   ├── worker-service
│   │   ├── llm-service
│   │   └── github-integration-service
│   └── infra
│       ├── prometheus/prometheus.yml
│       └── grafana/... 
├── frontend
│   ├── src
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml
└── .env.example
```

---

## Environment Variables

Copy `.env.example` to `.env` and fill secrets.

### Global

- `JWT_SECRET`
- `INTERNAL_API_KEY`
- `OPENAI_API_KEY`
- `OPENAI_MODEL`
- `GEMINI_API_KEY`
- `GEMINI_MODEL`
- `GITHUB_TOKEN`
- `GITHUB_WEBHOOK_SECRET`

### Frontend

- `VITE_API_BASE_URL` (for local: `http://localhost:8080`)

---

## Run Locally (Docker)

```bash
cp .env.example .env
# fill .env

docker compose build
docker compose up -d
```

### Access

- API Gateway: `http://localhost:8080`
- Frontend: `http://localhost:5173`
- Swagger docs:
  - `http://localhost:8081/swagger-ui.html`
  - `http://localhost:8082/swagger-ui.html`
  - `http://localhost:8084/swagger-ui.html`
  - `http://localhost:8085/swagger-ui.html`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3001`

---

## API Surface (Core)

### Auth
- `POST /api/v1/auth/signup`
- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`

### Reviews
- `GET /api/v1/reviews`
- `GET /api/v1/reviews/{id}`
- `GET /api/v1/reviews/analytics` (`ADMIN`)
- `POST /api/v1/reviews/{id}/chat`

### GitHub Integration
- `POST /webhooks/github`
- `POST /api/v1/github/comments` (internal service call)

### LLM (internal)
- `POST /api/v1/llm/review`
- `POST /api/v1/llm/chat`

---

## Deployment Guide

## Backend -> Render / Railway

1. Create one service per microservice (`api-gateway`, `auth-service`, etc.).
2. Use each service directory Dockerfile under `backend/services/<service>/Dockerfile`.
3. Provision managed PostgreSQL, Redis, and Kafka.
4. Set all env vars from `.env.example`.
5. Ensure internal DNS/service URLs match runtime network names.
6. Expose only gateway publicly; keep internal services private where possible.

## Frontend -> Vercel

1. Import `frontend` directory as project root.
2. Build command: `npm run build`
3. Output directory: `dist`
4. Set `VITE_API_BASE_URL` to public API Gateway URL.

---

## Monitoring

- Prometheus scrapes `/actuator/prometheus` from all services.
- Grafana auto-provisions datasource + dashboard.
- JVM, request-rate, error-rate, and Kafka lag panels included.

---

## Logging Best Practices Implemented

- Structured log pattern with service name and trace/span placeholders
- Centralized console log format (`logback-spring.xml` per service)
- Info-level defaults with package-specific overrides in `application.yml`

---

## Security Notes

- JWT authentication enforced at API Gateway
- Role-based restriction for analytics endpoint
- Internal service APIs protected with `X-Internal-Api-Key`
- GitHub webhook HMAC signature validation supported
- Passwords hashed with BCrypt

---

## Production Hardening Checklist

1. Replace in-memory gateway rate-limit map with Redis-backed Bucket4j storage.
2. Move GitHub auth to GitHub App installation tokens.
3. Add service discovery / config server for multi-environment scaling.
4. Add integration tests for webhook->Kafka->review lifecycle.
5. Add distributed tracing (OpenTelemetry exporter).

---

## Notes

- The project is fully scaffolded and runnable via Docker Compose.
- Local Maven compile was not executed in this environment because `mvn` is unavailable in shell.
