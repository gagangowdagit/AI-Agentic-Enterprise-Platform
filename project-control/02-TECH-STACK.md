# Technology Stack

This is the final technology snapshot as of 2026-10-10. Implemented means present in the repository; out-of-scope means intentionally not delivered in this project.

## Implemented Now

| Area | Technology | Status |
|---|---|---|
| Frontend | React 19, TypeScript, Vite | Implemented |
| Frontend routing | React Router | Implemented |
| Backend | Java 21, Spring Boot 4.1.1, Spring MVC | Implemented |
| API security | Spring Security, JJWT 0.12.6, stateless HS256 bearer access tokens | Implemented |
| API validation | Jakarta Validation | Implemented |
| Persistence | PostgreSQL, Spring Data JPA, Hibernate | Implemented |
| Migrations | Flyway | Implemented |
| Documents | Apache PDFBox, Apache POI | Implemented |
| AI runtime | Ollama REST API | Implemented |
| Testing | JUnit 5, Mockito, Spring Boot Test | Implemented |
| Tooling | Maven Wrapper, npm, ESLint | Implemented |

## Current Project Scope

The project is currently built around the following stack:

- Java 21 + Spring Boot 4.1.1
- Spring MVC REST controllers
- Spring Data JPA + Hibernate + PostgreSQL
- Flyway database migration scripts
- React 19 + TypeScript + Vite
- React Router for routed app pages
- PDF/DOCX/TXT processing with Apache libraries
- Ollama for embeddings and grounded LLM generation
- JUnit + Mockito for backend verification

## Authentication Stack

- Spring Security stateless filter chain protects API routes by default.
- JJWT creates and validates HS256 access tokens; the login response includes a bearer token and expiry metadata.
- The signing key is supplied as Base64 through `JWT_SECRET`. The Windows local startup script creates and reuses an ignored `.env` key when needed.
- Access-token lifetime defaults to 15 minutes and is configurable with `JWT_ACCESS_TOKEN_LIFETIME`.
- BCrypt password hashing is configured for password storage; legacy plaintext-password compatibility remains in login.

## Explicitly Out of Scope

- Redis, MongoDB, pgvector/Qdrant, RabbitMQ, Kafka
- Docker Compose or Kubernetes deployment
- WebSocket real-time streaming
- Production CI/CD, deployment, and operational observability
- Refresh-token, revocation, fine-grained RBAC, and resource-ownership authorization

These are excluded from the final delivery; their absence is not an open implementation milestone.
