# Technical Decisions

This log records the decisions that shape the current implementation. It reflects implemented choices and ongoing constraints rather than future wish lists.

## DECISION-001: Spring Boot and Java

- **Date:** 2026-08-20
- **Decision:** Use Java 21 with Spring Boot 4.1.1 and Maven.
- **Reason:** Strong fit for typed enterprise APIs, dependency injection, persistence, validation, and tests.
- **Status:** ACCEPTED / IMPLEMENTED

## DECISION-002: React Frontend

- **Date:** 2026-08-20
- **Decision:** Use React 19, TypeScript, Vite, and React Router.
- **Reason:** Fast development and a clear page/service structure for the app interface.
- **Status:** ACCEPTED / IMPLEMENTED

## DECISION-003: PostgreSQL as the Current System of Record

- **Date:** 2026-08-20
- **Decision:** Use PostgreSQL with Spring Data JPA and Flyway for the project’s transactional and metadata storage.
- **Reason:** This matches the current enterprise data model and keeps schema evolution explicit.
- **Status:** ACCEPTED / IMPLEMENTED

## DECISION-004: Ollama for Local AI Development

- **Date:** 2026-09-08
- **Decision:** Use Ollama for both embeddings and grounded generation through a thin REST abstraction.
- **Reason:** It supports local experimentation without requiring cloud API keys or a complex deployment path.
- **Status:** ACCEPTED / IMPLEMENTED

## DECISION-005: Application-Side Similarity Search for the Current RAG Scope

- **Date:** 2026-09-08
- **Decision:** Keep vector comparison in application code using stored embedding values rather than a separate vector database.
- **Reason:** This is sufficient for the current repository scope and keeps the system simpler to reason about.
- **Status:** ACCEPTED / CURRENT LIMITATION

## DECISION-006: Meeting AI Summary as a Domain Feature

- **Date:** 2026-09-20
- **Decision:** Add transcript persistence and AI-generated meeting summaries to the meeting domain instead of treating them as a generic chat feature.
- **Reason:** Meeting intelligence is part of the product workflow and needs to be durable and reviewable in the app.
- **Status:** ACCEPTED / IMPLEMENTED

## DECISION-007: Keep the Current Architecture Monolithic Until Scale Demands Change

- **Date:** 2026-09-20
- **Decision:** Continue with a Spring Boot monolith and a React frontend until a concrete scale or deployment requirement justifies microservices or multiple infrastructure layers.
- **Reason:** The codebase is already coherent and testable in this shape, and the current priority is product functionality rather than architecture complexity.
- **Status:** ACCEPTED / IN FORCE

## Decision Change Policy

When a decision changes, add a new entry with the reason, impact, and affected modules. Unimplemented proposals should not be described as current architecture or delivery status.
