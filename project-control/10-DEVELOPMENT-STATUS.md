# Development Status

**As of:** 2026-09-08
**Overall state:** IN PROGRESS, usable development slice
**Release status:** pre-release / not production ready

## What Works Today

- Spring Boot 4.1.1 backend on Java 21 with Maven Wrapper.
- React 19 + TypeScript + Vite frontend with routed enterprise pages.
- PostgreSQL/JPA persistence and Flyway migrations.
- Layered controllers, services, repositories, DTO validation, response envelopes, and global exception handling.
- User registration/login service flows and password hashing support.
- Project, department, team, document, timeline, analytics, and AI insight slices.
- PDF/DOCX/TXT document extraction, chunking, embedding persistence, and processing services.
- Ollama `nomic-embed-text` query embeddings and `llama3.2:3b` grounded generation.
- Project/global top-K retrieval with source metadata and a Nova AI frontend query flow.
- OpenAPI baseline and a broad JUnit/Mockito/Spring test suite.

## Current Gaps

- Full Spring Security enforcement, JWT lifecycle, RBAC, and authorization tests.
- Complete task/user/project CRUD polish, DTO boundaries, ownership checks, and pagination.
- Production document upload lifecycle, file storage, processing status, and idempotent reprocessing.
- Durable conversations, streaming, citations UI, prompt/model versioning, and AI evaluation.
- High-scale vector search, asynchronous jobs, retries, cancellation, notifications, and observability.
- CI/CD, containerized deployment, secret management, backups, and production hardening.

## Status by Capability

| Capability | Status |
|---|---|
| Repository and backend foundation | DONE |
| Frontend shell and core pages | PARTIAL / ACTIVE |
| PostgreSQL persistence and migrations | DONE for current schema |
| API conventions and validation | DONE baseline |
| Authentication | PARTIAL |
| Authorization/RBAC | NEXT |
| Project/team/document workflows | PARTIAL |
| Document extraction and chunking | DONE baseline |
| Ollama embedding and generation | DONE local integration |
| RAG retrieval and grounded response | PARTIAL, local scale |
| Agent workflows | PARTIAL |
| Analytics and insights | PARTIAL |
| Conversation memory | PLANNED / partial service concepts |
| Async messaging and real-time updates | PLANNED |
| Automated quality pipeline | NEXT |
| Production deployment | PLANNED |

## Immediate Priority Order

1. Fix the current failing backend test and establish a clean baseline.
2. Complete security boundaries and authorization before expanding AI mutations.
3. Harden document upload/processing and RAG failure handling.
4. Improve Nova AI conversations, sources, and evaluation.
5. Add integration tests, observability, and repeatable local deployment.

## Verification Snapshot

The focused Ollama embedding and LLM client tests pass. Direct local smoke tests for `/api/embed` and `/api/generate` pass when Ollama is running with the configured models. The full Maven suite currently contains an unrelated failing `ProjectDocumentsControllerTest` assertion for a missing `projectId` response field; this is tracked as technical debt and prevents a clean overall baseline.
