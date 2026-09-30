# Agile Delivery Status

This document reflects the current repository state as of 2026-09-30. It is intentionally limited to features that are implemented, in active work, or genuinely next-priority work. The detailed verification snapshot is maintained in `10-DEVELOPMENT-STATUS.md`.

## Status Legend

- DONE: implemented and present in the source tree
- IN PROGRESS: partially built and being finished
- NEXT: not yet complete but clearly prioritized

## Completed Work

### Phase 1: Foundation [DONE]

- Repository structure and working backend/frontend modules
- Java 21 + Spring Boot backend with Maven wrapper
- React + TypeScript + Vite frontend with routed pages
- REST API conventions with validation and response handling
- PostgreSQL/JPA/Flyway relational persistence

### Phase 2: Core Enterprise Features [DONE]

- User registration and login flows
- Project, department, and team management
- Document management, upload, and project-scoped retrieval
- Analytics pages and project insight endpoints
- Meeting lifecycle management
- Google Meet link handling
- Meeting participants and agenda support
- Backend task persistence, project task summaries, agent task tools, and task-triggered notifications; user-facing task management is not implemented

### Phase 3: Meeting Intelligence [DONE]

- Meeting create/update/cancel/delete flow
- Transcript upload and storage
- AI summary generation for stored transcripts
- Summary regeneration and persistence
- Meeting detail UI for transcript and summary review

### Phase 4: RAG and AI [DONE]

- Document ingestion and chunk processing
- Ollama embedding pipeline
- Similarity-based retrieval for project documents
- Grounded LLM answer generation
- AI project queries via Nova AI UI

### Phase 5: Test and Build Validation [PARTIAL]

- Frontend production build passes
- Backend suite currently has two failing tests; see the verification snapshot in `10-DEVELOPMENT-STATUS.md`

## In Progress / Not Yet Production-Complete

- Full security and authorization boundary using Spring Security/JWT
- Separate organizational position from application access role; prevent public self-assignment of elevated access
- Production-grade file lifecycle, ownership enforcement, and robust upload status tracking
- Complete task ownership and user-facing task workflows
- Larger AI evaluation and prompt safety checks
- Observability and operational telemetry

## Current Priorities

1. Separate employee position from application access role and make registration least-privileged.
2. Enforce server-side authentication and resource-level authorization; remove plaintext-password fallback.
3. Resolve the two failing backend tests.
4. Complete task ownership and user-facing task workflows; then harden document/AI operations and production observability.

## Definition of Done

A feature is considered complete only when the code, tests, API behavior, UI behavior, and project-control documentation are aligned.
