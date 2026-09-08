# Requirements and Scope

This document separates current product behavior from planned acceptance criteria. It is intentionally honest about the project being in progress.

## Current Functional Requirements

- Users can register and authenticate through the current application flows.
- The platform exposes versioned REST APIs for core enterprise data.
- Projects, departments, teams, documents, timelines, analytics, and selected AI insights are represented in backend services and frontend pages.
- Documents can be extracted from supported PDF, DOCX, and text inputs, split into chunks, and embedded through Ollama.
- Users can ask project-scoped questions through Nova AI; the backend retrieves relevant chunks and asks an Ollama model for a grounded answer.
- Responses use a standard success/error contract and validate request data.

## Near-Term Acceptance Requirements

### Identity and access

- Passwords are hashed and never returned.
- Protected routes require a valid session/token.
- Project, document, analytics, and AI access is authorized by user and project membership.
- Authorization failures are tested, logged safely, and returned consistently.

### Core operations

- Projects and tasks support validated CRUD, assignment, status, priority, dates, filtering, and pagination.
- Team membership and document ownership are enforced at the service boundary.
- DTOs prevent persistence entities and secrets from becoming public API contracts.

### Knowledge and RAG

- Uploads enforce type, size, ownership, and safe filename rules.
- Processing reports queued, running, completed, and failed states.
- Reprocessing is idempotent and old chunks/embeddings are not orphaned.
- Retrieval is scoped to authorized projects, applies a relevance policy, and returns source metadata.
- The assistant clearly reports when the available context does not support an answer.
- Ollama outages, missing models, timeouts, and malformed responses produce actionable API errors.

### Assistant and agents

- Conversations can be stored and retrieved per authorized user/project.
- Streaming or incremental progress is added only after durable chat behavior is stable.
- Any mutating AI tool requires schema validation, authorization, audit logging, idempotency, and user confirmation.
- Agent execution supports bounded retries, cancellation, and failure states.

## Non-Functional Requirements

- No secrets in Git or logs.
- Database changes use Flyway migrations.
- APIs remain testable independently of Ollama through mocks.
- The local setup is reproducible on Windows and documented.
- Critical paths have unit and integration coverage.
- Production work must add observability, rate limits, backups, retention rules, and threat modeling.

## Out of Current Scope

MongoDB, Redis, Qdrant, RabbitMQ, WebSockets, cloud hosting, Kubernetes, and full autonomous multi-agent orchestration are roadmap options, not current requirements. They should be introduced only when a measured product or scale requirement supports them.
