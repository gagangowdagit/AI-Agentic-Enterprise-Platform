# Technology Stack

## 1. Frontend

* React
* TypeScript
* Vite
# Technology Stack

Status labels in this file are deliberate: **Implemented** means present in the repository; **Planned** means a future option, not a current dependency.

## Implemented Now

| Area | Technology | Evidence / use |
|---|---|---|
| Frontend | React 19, TypeScript 6, Vite 8 | `frontend/package.json`, page and service modules |
| Routing | React Router 7 | Frontend route composition |
| Backend | Java 21, Spring Boot 4.1.1, Spring MVC | `rag-backend/pom.xml` and application bootstrap |
| API validation | Jakarta Validation | Request DTO constraints and global error responses |
| Persistence | PostgreSQL, Spring Data JPA, Hibernate | Relational entities and repositories |
| Migrations | Flyway PostgreSQL | Versioned SQL migrations in backend resources |
| API documentation | springdoc OpenAPI 2.8.9 | Swagger/OpenAPI configuration |
| Documents | Apache PDFBox 2.0.32, Apache POI 5.3.0 | PDF/DOCX extraction plus text processing |
| AI runtime | Ollama REST API | `nomic-embed-text` embeddings and `llama3.2:3b` generation |
| Backend tests | JUnit 5, Mockito, Spring Boot Test | Unit and controller tests |
| Tooling | Maven Wrapper, npm, ESLint | Repeatable local builds and linting |

## Implemented in a Limited or Local Form

- Authentication and user registration/login flows exist, but the full Spring Security filter chain, JWT lifecycle, and authorization policy are not complete.
- RAG stores embeddings in the current relational model and performs application-side similarity search; Qdrant is not connected.
- Document storage and processing are synchronous/local-development oriented.
- Agent and AI service classes exist for selected project workflows, but they are not yet a complete autonomous multi-agent platform.

## Planned Options, Not Current Dependencies

MongoDB, Redis, Qdrant, RabbitMQ, WebSocket/STOMP, React Testing Library, Playwright, Testcontainers, Docker Compose, GitHub Actions, AWS, Kubernetes, Prometheus, Grafana, and Spring AI may be introduced when a concrete requirement justifies them. Adding a technology requires a decision entry, implementation, tests, and updated documentation.

## Principles

Prefer the existing Spring/React/PostgreSQL/Ollama stack until scale, reliability, or product requirements demonstrate the need for another component. Keep boundaries modular so future vector storage, queues, caching, and model providers can be added without rewriting domain behavior.
* Other transactional data
