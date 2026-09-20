# Technology Stack

Status labels in this file are deliberate: Implemented means present in the repository; Planned means a future option, not a current dependency.

## Implemented Now

| Area | Technology | Status |
|---|---|---|
| Frontend | React 19, TypeScript, Vite | Implemented |
| Frontend routing | React Router | Implemented |
| Backend | Java 21, Spring Boot 4.1.1, Spring MVC | Implemented |
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

## Current Limitations

These are not active dependencies of the current project implementation:

- Full Spring Security and JWT lifecycle
- Redis, MongoDB, pgvector/Qdrant, RabbitMQ, Kafka
- Docker Compose or Kubernetes deployment
- WebSocket real-time streaming
- A production CI/CD pipeline

These can be introduced later when the project requires them, but they are not part of the current codebase or delivery claim.

## Principles

Prefer the existing Spring/React/PostgreSQL/Ollama stack until scale, reliability, or product requirements justify a new component. Keep boundaries modular so future vector storage, job queues, or orchestration layers can be added without rewriting business logic.
