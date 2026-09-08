# Technical Decisions

This log records decisions that affect implementation. Proposed technologies are not accepted decisions until they are implemented or explicitly approved for the next phase.

## DECISION-001: Spring Boot and Java

- **Date:** 2026-08-20
- **Decision:** Use Java 21 with Spring Boot 4.1.1 and Maven.
- **Reason:** Strong fit for typed enterprise APIs, dependency injection, persistence, validation, and testing.
- **Status:** ACCEPTED / IMPLEMENTED

## DECISION-002: React Frontend

- **Date:** 2026-08-20
- **Decision:** Use React 19, TypeScript, Vite, and React Router.
- **Reason:** Fast local development and a clear component/page/service structure.
- **Status:** ACCEPTED / IMPLEMENTED

## DECISION-003: PostgreSQL as Current System of Record

- **Date:** 2026-08-20
- **Decision:** Use PostgreSQL with Spring Data JPA and Flyway for current transactional and RAG metadata.
- **Reason:** Relational project data, migrations, constraints, and local simplicity are the current priority.
- **Status:** ACCEPTED / IMPLEMENTED

## DECISION-004: Ollama for Local AI Development

- **Date:** 2026-09-08
- **Decision:** Use Ollama through a small REST client abstraction with `nomic-embed-text` and `llama3.2:3b` defaults.
- **Reason:** Local, inspectable model execution without API keys; easy to test with mocked HTTP responses.
- **Impact:** Ollama is a required local runtime dependency for live RAG queries, while unit tests remain provider-independent.
- **Status:** ACCEPTED / IMPLEMENTED

## DECISION-005: Application-Side Similarity Search Initially

- **Date:** 2026-09-08
- **Decision:** Persist embeddings in the current relational model and calculate similarity in application code initially.
- **Reason:** Keeps the first working RAG slice small and avoids premature vector infrastructure.
- **Impact:** The design is suitable for development and small datasets, but requires measurement before scale.
- **Status:** ACCEPTED / CURRENT LIMITATION

## DECISION-006: Defer Infrastructure Expansion

- **Date:** 2026-09-08
- **Decision:** Treat MongoDB, Redis, Qdrant/pgvector, RabbitMQ, WebSockets, cloud, and Kubernetes as conditional roadmap options.
- **Reason:** They are not currently required by the implemented product slice and would add operational cost.
- **Impact:** Each future addition requires a concrete use case, migration plan, tests, and operational ownership.
- **Status:** ACCEPTED / ROADMAP POLICY

## Decision Change Policy

When a decision changes, add a new entry with the reason, affected modules, migration plan, and superseded decision. Never present an unimplemented proposal as current architecture.
