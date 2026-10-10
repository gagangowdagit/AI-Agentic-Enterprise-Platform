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

## DECISION-008: Separate Organizational Position from Application Access Role

- **Date:** 2026-09-30
- **Decision:** Model an employee's organizational position independently from the role used for application authorization. Public registration must not allow users to grant themselves elevated access; it must assign the least-privileged access role.
- **Reason:** The current employee `role` is used both as a job title and as a frontend access-gating value, while the backend does not enforce authorization. These are separate concepts and must not be conflated.
- **Status:** NOT IMPLEMENTED / OUT OF SCOPE FOR FINAL DELIVERY

## DECISION-009: Stateless JWT Access Authentication

- **Date:** 2026-10-10
- **Decision:** Issue short-lived HS256 JWT access tokens at login and validate bearer tokens with a stateless Spring Security filter chain. Keep the local signing key in the ignored backend `.env` file, generated once by the Windows startup script when no key is configured.
- **Reason:** Protect the delivered API flows with authentication while keeping the local development setup simple and avoiding server-side session storage.
- **Status:** ACCEPTED / IMPLEMENTED
- **Boundary:** No refresh tokens, revocation, logout endpoint, fine-grained role authorities, or resource ownership policy are included.

## DECISION-010: Close the Local-Demo Delivery Scope

- **Date:** 2026-10-10
- **Decision:** Close the planned local-demo/portfolio project scope at the final documented snapshot. Do not treat production-scale backlog ideas as incomplete promises or completed features.
- **Reason:** Preserve a clear and honest final record of delivered capabilities, known limitations, and verification results.
- **Status:** ACCEPTED / CLOSED

## Decision Change Policy

If a separately scoped future project changes a decision, add a new entry with the reason, impact, and affected modules. Unimplemented proposals must not be described as current architecture or delivery status.
