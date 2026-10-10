# AI Agentic Enterprise Platform — Project Overview

**Snapshot:** 2026-10-10  
**Project state:** Final local-demo / portfolio snapshot. The planned project scope is closed.  
**Important boundary:** This is not a production SaaS platform. Authentication is implemented, but full role-based and per-resource authorization is not.

This document is a self-contained technical orientation for developers and AI assistants. It describes the repository's implemented behavior and explicitly calls out important limitations so that readers do not mistake plans or demo behavior for shipped production controls.

## 1. What the project is

The AI Agentic Enterprise Platform is a web application for organizing enterprise project work and asking grounded questions over project documents. It combines:

- A React frontend for login, project and team operations, departments, documents, meetings, analytics, and Nova AI.
- A Java/Spring REST backend that validates requests and implements business logic.
- PostgreSQL as the relational system of record, with schema changes managed by Flyway.
- A document ingestion and retrieval-augmented generation (RAG) path using local Ollama models.
- Selected agent/task operations, meeting intelligence, project insights, and notifications.

The application is intentionally a local-demo/portfolio-scale monolith. It demonstrates an end-to-end product slice rather than cloud-scale distributed infrastructure or production enterprise access governance.

## 2. Current product capabilities

### User experience

The frontend has routes for:

| Route | Purpose |
|---|---|
| `/` and `/login` | Sign in |
| `/signup` and `/register` | Register an account |
| `/home` | Application home and project overview |
| `/projects` | Browse and manage projects |
| `/projects/:projectId` | Project details and related operations |
| `/documents` | Upload and browse project documents |
| `/departments` | Department views and management |
| `/meetings` | Browse/manage meetings |
| `/meetings/create` | Create a meeting |
| `/meetings/:meetingId` | Meeting details, transcript, and summary |
| `/meetings/:meetingId/edit` | Edit a meeting |
| `/nova-ai` | Ask questions about project knowledge |
| `/analytics` | Company and project analytics |

The shared shell provides navigation, profile/logout controls, and a persisted light/dark theme preference. Some UI actions are role-aware in the frontend; those presentation checks must not be treated as authorization.

### Enterprise workflows

- Projects: create, list, inspect, update, and delete; project details include related team, documents, timeline, knowledge, tasks, and insights endpoints.
- Organization: department and employee data, and project teams with available-member listing.
- Documents: upload, list by project, download, delete, and process PDFs, DOCX, and text files.
- Meetings: create/update/cancel/delete, agenda and participant information, meeting links, transcript storage, and AI summary generation/regeneration.
- Analytics: company and department/employee summaries, project analytics, timelines, and project insights.
- AI: project-scoped and global document retrieval for grounded answers; selected agent tools can inspect project data and create/update tasks.
- Notifications: list user notifications, list unread notifications, and mark a notification read.

Task creation, summaries, selected agent operations, and notifications exist in backend code. There is no complete general-purpose task-management UI or complete authenticated task-ownership workflow.

## 3. Technology stack

| Layer | Technology | Use |
|---|---|---|
| Frontend | React 19, TypeScript 6, Vite 8 | SPA UI and local development/build |
| Routing | React Router 7 | Client-side page routes |
| Backend | Java 21, Spring Boot 4.1.1, Spring MVC | REST API and application runtime |
| Security | Spring Security, JJWT 0.12.6 | Stateless HS256 bearer access tokens |
| Persistence | PostgreSQL, Spring Data JPA, Hibernate | Relational application data |
| Schema migrations | Flyway | Versioned database evolution |
| Document parsing | Apache PDFBox 2.0.32, Apache POI 5.3.0 | PDF and Office document extraction |
| AI runtime | Ollama HTTP API | Local embedding and language-model inference |
| Embedding model | `nomic-embed-text` | Document and query embeddings |
| Language model | `llama3.2:3b` | Grounded answer generation and AI summaries |
| Backend tests | JUnit 5, Mockito, Spring Boot Test | Unit and integration tests |
| Frontend quality | TypeScript compiler, Vite, ESLint | Build and lint |
| Build tools | Maven Wrapper, npm | Reproducible project commands |

External vector databases, Redis, message queues, WebSockets, containers, Kubernetes, cloud infrastructure, and production CI/CD are not part of the delivered stack.

## 4. Repository map

```text
.
├── frontend/
│   └── src/
│       ├── pages/          # Routed product pages
│       ├── components/     # Shared UI components
│       ├── services/       # HTTP/API client modules
│       └── utils/          # Frontend role and utility logic
├── rag-backend/
│   ├── src/main/java/com/rag/ragbackend/
│   │   ├── auth/           # Login, JWT service, request filter
│   │   ├── config/         # Security, password, web, agent, OpenAPI config
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Domain service interfaces/implementations
│   │   ├── repository/     # Spring Data repositories
│   │   ├── entity/         # JPA entities
│   │   ├── processing/     # Extraction, chunking, embeddings, RAG, LLM
│   │   ├── agent/          # Agent coordinator and tools
│   │   ├── memory/         # Persisted project memories
│   │   ├── execution/      # Agent execution status and streaming
│   │   └── exception/      # API/application error handling
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/   # Flyway V1–V28 schema/data migrations
│   └── run-backend.ps1     # Windows startup and local JWT key setup
├── project-control/        # Final status, architecture, requirements, decisions
├── README.md               # Local setup and first entry point
└── overview.md             # This complete-project orientation
```

## 5. Architecture

### Runtime shape

```text
Browser (React / TypeScript / Vite)
    |
    | HTTP JSON / multipart; Authorization: Bearer <JWT>
    v
Spring Boot REST API (Java 21)
    |
    +--> Spring Security JWT filter
    +--> Controllers -> request DTO validation -> services
    |                                  |
    |                                  +--> Spring Data repositories -> PostgreSQL
    |                                  +--> PDF/DOCX/TXT extraction and chunking
    |                                  +--> Ollama embeddings / language model
    |                                  +--> selected project agent tools
    v
JSON / file / SSE response
```

### Backend organization

The backend is a modular monolith. Controllers map HTTP endpoints and bind/validate inputs; services implement application and domain behavior; repositories use JPA to access PostgreSQL. Flyway owns database schema evolution, while `spring.jpa.hibernate.ddl-auto=none` avoids treating Hibernate as the schema migration authority.

Cross-cutting areas include Spring Security, CORS configuration, password encoding, OpenAPI configuration, shared response DTOs, and global exception handling. Not every controller uses the same response envelope: endpoints return their declared DTO/entity/list/empty response, while selected AI and execution APIs use `ApiResponse`.

### Frontend organization

The React app mounts through `main.tsx`, and `App.tsx` wires the router and shared navigation/profile/theme shell. Page-specific API calls live under `frontend/src/services`. Most protected API calls use the shared `apiFetch` client to attach the session's bearer token and dispatch an authentication-expired event on HTTP 401.

### Data model overview

The relational schema is versioned with 28 Flyway migrations. Main domain areas include:

- Employee accounts, department structure, and projects.
- Project teams and task assignments.
- Documents, extracted text, document chunks, and chunk embeddings.
- Meetings, participants/agenda data, transcripts, and generated summaries.
- Notifications, project memories, and agent execution records.

Embeddings and their associated document data are persisted relationally; similarity search is application-side. JWT access tokens and the signing key are not stored in PostgreSQL. There is no durable user chat/conversation store.

## 6. API catalogue

### API conventions

- Base URL in local development: `http://localhost:8080`.
- Main API prefix: `/api/v1`.
- JSON is used for ordinary request/response bodies. Document upload uses `multipart/form-data`; agent execution streaming uses Server-Sent Events.
- Request DTOs use Jakarta validation in selected endpoints. Error handling is centralized, but the exact response shape can differ by controller.
- OpenAPI/Swagger UI is included by the backend dependency. Security configuration requires authentication for all routes except the explicit public exceptions below, so API documentation endpoints may also require a bearer token.

### Authentication and authorization controls

`SecurityConfig` disables CSRF, form login, and HTTP Basic; configures stateless sessions; installs `JwtAuthenticationFilter`; returns HTTP 401 for unauthenticated protected requests; and requires authentication for all routes not explicitly allowed.

**Public routes in the current security configuration:**

| Method | Path | Reason |
|---|---|---|
| `OPTIONS` | `/**` | CORS preflight |
| `POST` | `/api/v1/auth/login` | Login |
| `GET` | `/api/health` | Health |
| `GET` | `/api/v1/health` | Health |
| `GET` | `/api/v1/departments` | Department listing |
| `POST` | `/api/v1/employees` | Employee registration |

All other API routes require `Authorization: Bearer <accessToken>`. A token subject contains the employee ID. The current filter validates token signature and expiry and sets an authenticated principal, but does not assign authorities from the employee role. Fine-grained RBAC, project membership/ownership checks, and per-user resource isolation are not implemented. Public employee creation and department listing should not be misinterpreted as production-safe access policies.

### Endpoint inventory

Unless listed in the public table above, assume the endpoint requires a valid bearer JWT.

#### Authentication, identity, and status

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/auth/login` | Validate credentials; return user details and JWT token/expiry metadata. Public. |
| `POST` | `/api/v1/users` | User creation endpoint. |
| `GET` | `/api/v1/employees` | List employees. |
| `POST` | `/api/v1/employees` | Create/register employee. Public in current security configuration. |
| `GET` | `/api/v1/departments` | List departments. Public in current security configuration. |
| `POST` | `/api/v1/departments` | Create department. |
| `GET` | `/api/health` | Health response. Public. |
| `GET` | `/api/v1/health` | Health response. Public. |
| `GET` | `/api/system/status` | System status endpoint. |
| `GET` | `/api/v1/system/status` | System status endpoint alias. |
| `POST` | `/api/validate` | Example request-validation endpoint. |

#### Projects, teams, documents, and project intelligence

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/projects` | List projects. |
| `POST` | `/api/v1/projects` | Create project. |
| `GET` | `/api/v1/projects/{projectId}` | Get a project. |
| `GET` | `/api/v1/projects/{projectId}/details` | Get project details. |
| `PUT` | `/api/v1/projects/{projectId}` | Update a project. |
| `DELETE` | `/api/v1/projects/{projectId}` | Delete a project. |
| `GET` | `/api/v1/projects/{projectId}/team` | List project team members. |
| `GET` | `/api/v1/projects/{projectId}/team/available` | List employees available to add to the team. |
| `POST` | `/api/v1/projects/{projectId}/team` | Add a team member. |
| `DELETE` | `/api/v1/projects/{projectId}/team/{employeeId}` | Remove a team member. |
| `GET` | `/api/v1/projects/{projectId}/documents` | Get project documents and metadata. |
| `GET` | `/api/v1/projects/{projectId}/timeline` | Get project timeline. |
| `GET` | `/api/v1/projects/{projectId}/task-summary` | Get project task summary; not general task CRUD. |
| `GET` | `/api/v1/projects/{projectId}/knowledge-summary` | Get project knowledge summary. |
| `GET` | `/api/v1/projects/{projectId}/ai-insights` | Get project AI insights. |
| `POST` | `/api/v1/analytics/projects/{projectId}` | Calculate/query project analytics. |
| `GET` | `/api/v1/reports/projects/{projectId}` | Get project report. |

#### Documents and processing

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/documents?projectId={id}` | List documents for a project. `projectId` is required. |
| `POST` | `/api/v1/documents` | Upload a multipart file with `projectId` and `file` fields. |
| `GET` | `/api/v1/documents/{id}/download` | Download a stored document. |
| `DELETE` | `/api/v1/documents/{id}` | Delete a document. |
| `POST` | `/api/v1/documents/{id}/process` | Process a document, including extraction/chunking/embedding as configured. |

The supported extraction paths include PDF, DOCX, and plain text. Exact validation limits and file lifecycle guarantees are implementation-specific; there is no production-grade background upload queue in this delivery.

#### Meetings and meeting AI

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/meetings` | List meetings. |
| `POST` | `/api/v1/meetings` | Create a meeting. |
| `GET` | `/api/v1/meetings/{meetingId}` | Get meeting details. |
| `PUT` | `/api/v1/meetings/{meetingId}` | Update meeting details. |
| `PATCH` | `/api/v1/meetings/{meetingId}/cancel` | Cancel a meeting. |
| `POST` | `/api/v1/meetings/{meetingId}/transcript` | Save transcript JSON (`transcriptText`, optional `fileName`). |
| `POST` | `/api/v1/meetings/{meetingId}/summary/regenerate` | Regenerate the meeting AI summary. |
| `DELETE` | `/api/v1/meetings/{meetingId}` | Delete a meeting. |

The meeting workflow manages Google Meet link values; that does not mean this project implements a complete Google Calendar/Meet integration.

#### Analytics

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/analytics/overview` | Company analytics overview. |
| `GET` | `/api/v1/analytics/departments` | Department analytics. |
| `GET` | `/api/v1/analytics/employees` | Employee analytics. |

#### RAG, project AI, and agent executions

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/rag/query` | Retrieve relevant project/global document chunks and generate a grounded answer. |
| `POST` | `/api/v1/ai/projects/{projectId}/query` | Process a project request through the project manager/agent workflow. |
| `GET` | `/api/v1/agents/executions/{executionId}` | Get an agent execution status. |
| `GET` | `/api/v1/agents/executions/{executionId}/stream` | Receive execution updates as `text/event-stream` SSE. |

The Nova UI sends a request shaped like:

```json
{
  "projectId": "project-id-or-global",
  "query": "What are the key project risks?",
  "topK": 3
}
```

`projectId` and `query` must be nonblank and `topK` must be at least 1. The Nova UI uses `topK: 3`. The API response is an `ApiResponse` wrapping the generated LLM response, including the answer and available retrieved context.

The agent-execution endpoint also streams execution status events over SSE. This is execution-status streaming, not token-by-token streaming of Nova answers.

#### Notifications

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/notifications/users/{userId}` | List notifications for a user ID. |
| `GET` | `/api/v1/notifications/users/{userId}/unread` | List unread notifications for a user ID. |
| `POST` | `/api/v1/notifications/{notificationId}/read` | Mark a notification read. |

These routes are authenticated but are not bound to the authenticated principal through an ownership policy in this project snapshot.

## 7. Authentication and JWT details

1. The user submits email/password to `POST /api/v1/auth/login`.
2. The backend loads the employee by email and verifies the credential. BCrypt hashes are recognized; legacy plaintext comparison remains as a compatibility fallback.
3. `JwtService` signs an HS256 JWT whose subject is the employee ID and whose issued/expiry times are set.
4. Login response fields include `accessToken`, `tokenType` (`Bearer`), `expiresIn`, and `expiresAt`, along with user information.
5. Frontend stores the token in `sessionStorage` under `rag-access-token`, attaches it to protected API calls, and clears it/redirects to login on HTTP 401.
6. Spring's stateless `JwtAuthenticationFilter` validates presented bearer tokens and supplies an authenticated principal; uncredentialed protected routes are rejected.

Configuration:

| Setting | Default / requirement | Purpose |
|---|---|---|
| `JWT_SECRET` | Base64 string decoding to at least 32 bytes | HS256 signing key |
| `JWT_ACCESS_TOKEN_LIFETIME` | `PT15M` | Access token lifetime as a duration |
| `OLLAMA_API_URL` | `http://localhost:11434` | Ollama service URL |
| `OLLAMA_EMBEDDING_MODEL` | `nomic-embed-text` | Embedding model |
| `OLLAMA_LLM_MODEL` | `llama3.2:3b` | Text generation model |
| `server.port` | `8080` | Backend HTTP port |
| Datasource | Local PostgreSQL `rag_db` configuration in `application.properties` | Database connection |

`run-backend.ps1` generates a random 32-byte key and writes it to ignored `rag-backend/.env` on first local startup if `JWT_SECRET` is not already set. It reuses the key on later starts. Keep `.env` private and untracked. Changing the key invalidates existing access tokens. Do not copy a real secret into documentation, Git, issue reports, or chat.

Security boundaries: tokens are readable by JavaScript in the browser because they are stored in `sessionStorage`; there is no HttpOnly cookie session, refresh-token flow, revocation, or server-side logout. The employee `role` is not converted into Spring Security authorities. The current JWT layer authenticates identity only; it is not complete authorization or production identity management.

## 8. Document intelligence and RAG

### Ingestion flow

```text
Upload file + project ID
  -> persist document metadata
  -> choose PDF / DOCX / text extractor
  -> extract text
  -> split text into chunks
  -> call Ollama embedding model
  -> persist chunks and embedding data in PostgreSQL
```

### Question-answering flow

```text
Question + project ID + topK
  -> embed question
  -> select candidate chunks within project scope
  -> calculate similarity in application code
  -> build a context-grounded prompt
  -> call Ollama language model
  -> return answer and available supporting context
```

The implementation is RAG, not model fine-tuning. Responses can be wrong or incomplete; grounding prompts do not guarantee truth. Live AI flows require a reachable Ollama server and the configured models. Embeddings are stored in the relational database and compared in application code; pgvector/Qdrant are not in use.

## 9. Local development

### Prerequisites

- Java 21+
- Node.js and npm
- PostgreSQL, with a `rag_db` database and credentials matching `rag-backend/src/main/resources/application.properties` (or local overrides)
- Ollama with `nomic-embed-text` and `llama3.2:3b`

### Start services (Windows PowerShell)

Start PostgreSQL as a service, then use a separate terminal for Ollama:

```powershell
ollama serve
ollama pull nomic-embed-text
ollama pull llama3.2:3b
```

Start the backend from its module directory:

```powershell
cd rag-backend
.\run-backend.ps1
```

Start the frontend in a separate terminal:

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. Backend base URL is `http://localhost:8080/api/v1`; Swagger UI is normally `http://localhost:8080/swagger-ui/index.html` (protected routes/docs may require a bearer token under the current security rules).

Useful checks:

```powershell
Invoke-WebRequest http://localhost:11434/api/tags
Invoke-WebRequest http://localhost:8080/api/v1/health
```

If startup reports that port 8080 is already in use, a backend instance is already listening or another process owns the port. Stop the intended process or configure a different server port before launching another copy.

## 10. Build and test

Frontend:

```powershell
cd frontend
npm run build
npm run lint
```

Backend:

```powershell
cd rag-backend
.\mvnw.cmd test
```

Final verification recorded on 2026-10-10:

- Frontend production build (`tsc -b && vite build`): passed.
- Backend suite: 163 tests; 161 passed, 2 failed, 0 errors, 0 skipped.
- Failing tests:
  - `AgentServiceTest.storesCompletedInteractionAsProjectMemory` — memory argument assertion mismatch.
  - `ProjectDocumentsControllerTest.returnsDocumentsForRequestedProjectWithMetadata` — expected project ID was null.
- JWT service, filter, and authentication integration tests passed in that run.

Do not report the backend test suite as fully green without rerunning it after fixing and verifying the two failures.

## 11. Known limitations and scope boundaries

- Authentication is implemented; full RBAC, role authority assignment, and per-project/resource ownership authorization are not.
- Login supports legacy plaintext-stored passwords as a compatibility fallback; migrating all accounts and removing that fallback was not completed.
- Public employee creation and department listing are explicit security exceptions in current configuration.
- Notifications accept user IDs in route paths but do not enforce ownership against the authenticated principal.
- Task-related APIs are summaries and selected agent tools, not a complete task CRUD/lifecycle user experience.
- No durable user conversation history, streaming chat, refresh tokens, revocation, or logout endpoint.
- Agent execution status has an SSE stream; generated chat answers themselves are not streamed.
- No asynchronous job queue, external vector database, production object storage, multi-tenant isolation, deployment automation, CI/CD pipeline, or production-grade observability.
- Local Ollama availability is required for AI features; output quality and grounding are not guaranteed.
- Backend suite has two known failing tests in the documented final run.

These are disclosed limitations of the final local-demo/portfolio delivery, not claims of completed production behavior or an active next-phase roadmap.

## 12. Source of truth and AI assistant guidance

When analyzing or changing this repository:

1. Treat implementation and tests as authoritative if documentation disagrees.
2. Treat this overview as orientation, not a substitute for inspecting the relevant source, DTO, tests, and migrations.
3. Do not claim that authentication implies authorization. Check `SecurityConfig` for public exceptions and remember that the principal has no role authorities.
4. Do not claim production readiness, complete RBAC, task CRUD UI, durable chat history, cloud deployment, or external vector storage.
5. Preserve API contracts and Flyway migration discipline; never expose or commit local `.env` values or secrets.
6. The final project snapshot and recorded test results are maintained in `project-control/10-DEVELOPMENT-STATUS.md`; architectural decisions are in `project-control/11-DECISIONS.md`.

For implementation details, see `rag-backend/src/main/java/com/rag/ragbackend/`, `frontend/src/`, and `rag-backend/src/main/resources/db/migration/`.
