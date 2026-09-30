# Requirements and Scope

This document reflects the currently implemented product scope for the repository as of 2026-09-30. See `10-DEVELOPMENT-STATUS.md` for the latest verification results.

## Implemented Functional Requirements

- Users can register and authenticate through the current application flows.
- The platform exposes versioned REST APIs for core enterprise data.
- Projects, departments, teams, documents, timelines, analytics, and AI insights are represented in backend services and frontend pages.
- Documents can be processed from PDF, DOCX, and text inputs, split into chunks, and embedded through Ollama.
- Users can ask project-scoped questions through Nova AI; the backend retrieves relevant chunks and asks an Ollama model for a grounded answer.
- Meeting lifecycle flows support create, update, cancel, delete, agenda, participants, and Google Meet-link management.
- Meeting transcripts can be uploaded and summarized using an LLM-backed summary flow.
- Tasks can be persisted and summarized for projects through backend services and agent tools; a complete task-management UI and ownership workflow are not implemented.
- Notifications can be retrieved and marked read through backend endpoints; task assignment and status changes can generate notifications.
- The platform returns consistent success/error payloads and validates request data.

## Current Scope Boundaries

### Identity and access

- Password hashing exists for user storage and login flows.
- Registration and login screens and API flows are implemented.
- The employee `role` field currently serves both as organizational position and as a frontend access-gating value; public registration allows selecting management values.
- Server-side authentication/authorization enforcement and project/resource boundary checks are not implemented. Frontend role checks are not a security boundary.
- Login retains a plaintext-password fallback for legacy stored values; password-hash migration/removal remains required.
- The target policy is to model organizational position separately from application access role and assign only least-privileged access through public registration; this is not implemented yet.

### Core operations

- Project, department, team, and document data flows are implemented.
- Meeting scheduling and management are implemented as a working feature slice.
- Task persistence and agent operations exist in the backend, but task ownership enforcement and the user-facing lifecycle are incomplete.
- DTOs are used at API boundaries to avoid exposing persistence internals.

### Knowledge and RAG

- Document uploads, chunking, and embeddings are implemented.
- Retrieval is project-scoped and source-aware.
- Ollama is used for embeddings and answer generation.
- Model/runtime failure behavior is handled at the application layer.

### Meeting AI

- Meeting transcript upload and stored transcript text are implemented.
- AI summary generation is implemented and can be regenerated.
- Summary and transcript data are persisted as part of the meeting record.

## Non-Functional Requirements in Scope

- No secrets in Git or logs.
- Database changes use Flyway migrations.
- APIs are testable independently of the local Ollama runtime.
- The local setup is reproducible on Windows and documented.
- Critical implementation paths have targeted automated coverage.

## Out of Current Scope

The following are not current implementation claims and should not be described as delivered features without separate implementation work:

- Full production authorization and RBAC
- JWT-based session policy
- Event streaming and chat persistence at scale
- Queue-based async processing
- Kubernetes or cloud deployment
- External vector database infrastructure
- Full autonomous multi-agent production orchestration
