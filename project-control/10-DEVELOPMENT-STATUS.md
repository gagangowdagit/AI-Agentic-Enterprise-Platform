# Development Status

**As of:** 2026-09-29
**Overall state:** Working product slice, not yet production-ready
**Release status:** pre-release / internal demo-grade implementation

## What Works Today

- Java 21 + Spring Boot backend with Maven wrapper and layered service architecture
- React 19 + TypeScript + Vite frontend with routed enterprise pages
- PostgreSQL/JPA/Flyway persistence for projects, teams, departments, documents, meetings, and AI metadata
- User registration and login flows; password hashing is implemented, with a legacy plaintext-password fallback still present
- Employee account roles returned by login and role-aware frontend navigation/actions
- Project, department, team, document, analytics, and insights modules
- Meeting scheduling, participants, agenda, Google Meet handling, and meeting management actions
- Transcript upload and AI summary generation for meetings
- Document extraction, chunking, embedding persistence, and retrieval-based AI queries
- Ollama-backed `nomic-embed-text` embeddings and `llama3.2:3b` answer generation
- Project-scoped grounded responses through Nova AI
- Updated application navigation and analytics views
- Frontend production build currently passes

## Current Gaps

- Backend authentication/authorization enforcement: role-aware controls currently run in the frontend only; no server-side API authorization policy is implemented
- Role model separation: employee `role` is used for both organizational role/title and frontend access gating; signup currently allows a registrant to select management roles
- Removal or migration of legacy plaintext-password support
- Fine-grained RBAC and project boundary authorization
- Durable conversation history and more advanced user-memory patterns
- Production-grade upload queueing, retries, and lifecycle status management
- Stronger AI evaluation, prompt safety, and retrieval quality checks
- Observability, CI/CD, and deployment hardening for production

## Status by Capability

| Capability | Status |
|---|---|
| Repository and backend foundation | DONE |
| Frontend shell and routed pages | DONE |
| PostgreSQL persistence and Flyway schema | DONE |
| Project, team, department, and document flows | DONE |
| Meeting lifecycle and detail flows | DONE |
| Transcript upload and meeting AI summary | DONE |
| Document extraction and chunking | DONE |
| Ollama embedding and generation | DONE |
| RAG retrieval and grounded answer flow | DONE |
| Agent workflows | DONE for selected project-scoped tasks |
| Analytics and insights | DONE |
| Registration and login with password hashing | PARTIAL; legacy plaintext fallback remains |
| Role-aware frontend controls | PARTIAL; client-side gating only |
| Authentication hardening and server-side authorization/RBAC | NEXT |
| Separate employee position from application access role | NEXT |
| Production observability | NEXT |
| Deployment automation | NEXT |

## Immediate Priority Order

1. Separate organizational position from application access role; prevent public self-assignment of privileged access.
2. Add server-side authentication and authorization enforcement, including project boundary checks; remove legacy plaintext-password fallback.
3. Resolve the two currently failing backend tests and keep the suite green.
4. Harden upload, document lifecycle, AI reliability, observability, and deployment readiness.

## Verification Snapshot

Latest local checks on 2026-09-29:

- `frontend`: `npm run build` passed (TypeScript and Vite production build)
- `rag-backend`: `./mvnw test` ran 150 tests; 148 passed and 2 failed, with no test errors or skips
- Failing tests: `AgentServiceTest.storesCompletedInteractionAsProjectMemory` (argument assertion) and `ProjectDocumentsControllerTest.returnsDocumentsForRequestedProjectWithMetadata` (`projectId` response was null)

This status reflects the implemented source and observed validation results, not future roadmap claims.
