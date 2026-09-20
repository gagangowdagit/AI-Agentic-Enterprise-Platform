# Requirements and Scope

This document reflects the currently implemented product scope for the repository as of 2026-09-20.

## Implemented Functional Requirements

- Users can register and authenticate through the current application flows.
- The platform exposes versioned REST APIs for core enterprise data.
- Projects, departments, teams, documents, timelines, analytics, and AI insights are represented in backend services and frontend pages.
- Documents can be processed from PDF, DOCX, and text inputs, split into chunks, and embedded through Ollama.
- Users can ask project-scoped questions through Nova AI; the backend retrieves relevant chunks and asks an Ollama model for a grounded answer.
- Meeting lifecycle flows support create, update, cancel, delete, agenda, participants, and Google Meet-link management.
- Meeting transcripts can be uploaded and summarized using an LLM-backed summary flow.
- The platform returns consistent success/error payloads and validates request data.

## Current Scope Boundaries

### Identity and access

- Password hashing exists for user storage and login flows.
- Session flow and auth screens are implemented in the app.
- Full Spring Security, JWT lifecycle, and RBAC enforcement remain next-step hardening work.

### Core operations

- Project, department, team, and document data flows are implemented.
- Meeting scheduling and management are implemented as a working feature slice.
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
