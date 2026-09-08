# Development Plan and Delivery Roadmap

This is the canonical roadmap. Status is based on the repository as of 2026-09-08. Work is intentionally incremental; future phases are proposals until implemented and verified.

## Status Legend

- `[DONE]` implemented and verified in the current repository
- `[PARTIAL]` a useful slice exists, but the production capability is incomplete
- `[NEXT]` the next high-value delivery target
- `[PLANNED]` future backlog item
- `[OPTIONAL]` a scale or product enhancement to evaluate later

## Phase 0: Foundation [DONE]

- Repository structure, module READMEs, project-control set, and Git workflow.
- Spring Boot application, Maven Wrapper, Java 21, API versioning, response envelope, validation, exception handling, and OpenAPI baseline.
- React/Vite/TypeScript application with routed pages and REST service modules.

## Phase 1: Core Operations [PARTIAL]

- `[DONE]` User entity, repository, registration/login service flows, password hashing support, and frontend login/register pages.
- `[DONE]` Project, department, team, document, timeline, analytics, and insight service/API slices.
- `[DONE]` PostgreSQL/JPA entities, repositories, Flyway migrations, and controller/service tests.
- `[PARTIAL]` Complete CRUD and UI workflows for projects, tasks, members, and users.
- `[NEXT]` Finish task lifecycle, pagination/filtering, ownership rules, DTO separation, and consistent authorization.

## Phase 2: Identity and Security [NEXT]

- Add Spring Security configuration and protected route policy.
- Implement short-lived access tokens, refresh-token rotation, logout/revocation, and frontend session handling.
- Define ADMIN, MANAGER, and EMPLOYEE permissions against projects, documents, analytics, and AI actions.
- Add negative authorization tests and remove sensitive fields from API responses.

## Phase 3: Document Knowledge Base [PARTIAL]

- `[DONE]` PDF/DOCX/TXT extraction, chunk persistence, document processing service, embedding service abstraction, and local Ollama embedding client.
- `[DONE]` Project-scoped/global retrieval, normalized vectors, top-K similarity selection, grounded prompt construction, and source metadata in responses.
- `[PARTIAL]` Upload/list/delete UX and processing lifecycle are not yet a complete production workflow.
- `[NEXT]` Add upload validation, file-size limits, processing status, idempotent reprocessing, document ownership, and robust failure reporting.

## Phase 4: RAG Reliability [NEXT]

- Add retrieval thresholds, chunk metadata, duplicate handling, prompt/context limits, and evaluation fixtures.
- Decide whether PostgreSQL storage is sufficient for the target scale; introduce pgvector or Qdrant only with measured need.
- Add model health checks, timeouts, retry policy, model configuration validation, and a useful degraded-mode API error.
- Add retrieval precision/recall examples and regression tests for grounding and source attribution.

## Phase 5: Nova AI Assistant [PARTIAL]

- `[DONE]` Nova AI frontend page sends project-scoped queries to `/api/v1/rag/query`.
- `[DONE]` Ollama generation client uses a grounded prompt and returns context/source data.
- `[PARTIAL]` Conversation history, streaming, citations UI, prompt versioning, and durable chat sessions.
- `[NEXT]` Persist conversations, add streaming or polling for long generations, expose sources in the UI, and improve user-facing error states.

## Phase 6: Controlled Agent Workflows [PARTIAL]

- `[DONE]` Agent/service classes and selected project-analysis/tool flows exist in the backend.
- `[NEXT]` Define a versioned tool contract with schemas, authorization, idempotency, audit events, timeouts, and explicit human confirmation for mutations.
- `[PLANNED]` Add execution records, retries, cancellation, supervisor routing, and specialized read-only agents.
- `[PLANNED]` Add task/project/report tools only after normal APIs and authorization are stable.

## Phase 7: Analytics, Memory, and Notifications [PARTIAL]

- `[DONE]` Project/company analytics and selected insight APIs/pages.
- `[PARTIAL]` Memory classes and service concepts exist; durable conversation/user memory is incomplete.
- `[NEXT]` Define analytics metrics, add trend/health calculations, persist AI execution metadata, and add source-aware reports.
- `[PLANNED]` Add in-app notifications, email integration, conversation summarization, user preferences, and retention controls.

## Phase 8: Async and Real-Time Processing [PLANNED]

- Introduce background document ingestion after the synchronous workflow is reliable.
- Add a job model, status transitions, retries, dead-letter handling, and idempotency.
- Evaluate Spring events, an executor, or RabbitMQ based on measured workload.
- Add WebSocket/SSE only when the UI has a real long-running status workflow to display.

## Phase 9: Quality and Operations [NEXT]

- Fix existing failing tests and establish a clean baseline before new features.
- Add frontend lint/build checks, API integration tests, PostgreSQL integration tests, and a small Playwright smoke suite.
- Add structured logs, correlation IDs, health/readiness checks, AI latency/error metrics, and safe logging policy.
- Add Docker Compose for reproducible PostgreSQL/Ollama development, then CI with build/test/security checks.

## Phase 10: Production Delivery [PLANNED]

- External object storage for documents, backups, migrations, secret management, and rate limits.
- Containerized frontend/backend deployment and environment-specific configuration.
- Evaluate managed PostgreSQL, object storage, observability, and a container platform such as ECS/EKS only after the local deployment is repeatable.
- Add threat modeling, privacy/retention policy, load testing, disaster recovery, and release automation.

## Definition of Done

A roadmap item is complete when code, tests, configuration, API/UI behavior, setup documentation, and status records agree. A proposed technology is not considered delivered because it appears in a plan.
