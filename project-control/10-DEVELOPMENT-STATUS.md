# Development Status

**As of:** 2026-09-30
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
- Backend task persistence, project task summaries, and agent task tools; there is no task-management frontend workflow yet
- Notification API and persisted task-assignment/status notifications
- Persisted project memories and agent execution records; these are distinct from durable chat conversation history
- Updated application navigation and analytics views
- Frontend production build currently passes

## Current Gaps

- Backend authentication/authorization enforcement: role-aware controls currently run in the frontend only; no server-side API authorization policy is implemented
- Role model separation: employee `role` is used for both organizational role/title and frontend access gating; signup currently allows a registrant to select management roles
- No generic task-management UI or authenticated task-ownership workflow
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
| Task persistence, summaries, and agent tools | PARTIAL; backend capability only, no task-management UI |
| Notifications | PARTIAL; backend endpoints and task-triggered notifications, no authorization boundary |
| Analytics and insights | DONE |
| Registration and login with password hashing | PARTIAL; legacy plaintext fallback remains |
| Role-aware frontend controls | PARTIAL; client-side gating only |
| Authentication hardening and server-side authorization/RBAC | NEXT |
| Separate employee position from application access role | NEXT |
| Production observability | NEXT |
| Deployment automation | NEXT |

## Immediate Priority Order

1. Separate organizational position from application access role; make public registration least-privileged and prevent self-assignment of elevated access.
2. Add server-side authentication and authorization enforcement, including project/resource boundary checks; remove legacy plaintext-password fallback.
3. Resolve the two currently failing backend tests and keep the suite green.
4. Complete task ownership and user-facing task workflows; harden uploads, AI reliability, observability, and deployment readiness.

## Verification Snapshot

Latest local checks on 2026-09-30:

- `frontend`: `npm run build` passed (TypeScript and Vite production build)
- `rag-backend`: `./mvnw test` ran 150 tests; 148 passed and 2 failed, with no test errors or skips
- Failing tests: `AgentServiceTest.storesCompletedInteractionAsProjectMemory` (argument assertion) and `ProjectDocumentsControllerTest.returnsDocumentsForRequestedProjectWithMetadata` (`projectId` response was null)

This status reflects the implemented source and observed validation results, not future roadmap claims.
