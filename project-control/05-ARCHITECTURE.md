# System Architecture

## Current Runtime Shape

```text
React 19 + TypeScript + Vite
          |
          | REST / JSON
          v
Spring Boot 4.1.1 / Java 21
          |
    Controllers
          v
    Services
          |
   Repository / JPA Layer
          |
          v
      PostgreSQL
          |
          v
   Document + AI processing
          |
          v
        Ollama
```

## Frontend

The frontend is organized around routed pages, reusable UI components, and API service modules. The current app covers authentication, home, projects, project details, departments, documents, meetings, analytics, and Nova AI. The UI uses a direct REST integration model with `fetch` calls to the backend services.

After login, the frontend stores the returned bearer access token in `sessionStorage` and includes it on protected API requests. The token remains accessible to page scripts; the frontend's role-aware controls are presentation behavior, not an authorization boundary.

## Backend

The backend follows a layered Spring architecture:

```text
Controller -> DTO/validation -> Service -> Repository -> PostgreSQL
                         |
                         +-> document processing
                         +-> embedding / retrieval services
                         +-> LLM generation
```

Core responsibilities include:

- login, JWT issuance, bearer-token validation, and authenticated request handling
- project, team, department, and document management
- meeting scheduling and lifecycle actions
- transcript upload and summary generation
- document extraction and project knowledge retrieval
- AI answer generation using local Ollama models

Global exception handling and API response wrappers keep success and failure behavior consistent across modules.

## Authentication Flow

```text
Email + password
  -> POST /api/v1/auth/login
  -> verify stored password
  -> issue signed HS256 JWT (employee ID subject, configured expiry)
  -> client sends Authorization: Bearer <token>
  -> stateless JwtAuthenticationFilter validates signature and expiry
  -> Spring Security requires authentication for protected routes
```

The local Windows launcher creates and reuses a Base64-encoded 32-byte signing key in the ignored backend `.env` file. The default access-token lifetime is 15 minutes. Tokens are not persisted. This is authentication, not complete authorization: application roles are not attached as security authorities, and project/resource ownership checks, refresh tokens, revocation, and logout are not implemented.

## Current RAG Flow

```text
Question
  -> embed query with Ollama
  -> load project documents/chunks
  -> calculate similarity and select top-K matches
  -> build grounded prompt
  -> call LLM for response
  -> return answer plus source context
```

Embeddings are currently stored in the relational model and compared in application code. This is a pragmatic local implementation suitable for the current repository scope.

## Final Delivery Boundaries

The architecture is intentionally monolithic and delivered as a local enterprise demo/portfolio project, not a multi-service production platform. The final scope does not include durable chat history, streaming, queue-based processing, an external vector store, production observability, or deployment automation.

## Architecture Principles

Keep domain behavior independent from model infrastructure. Prefer explicit service interfaces, validated DTOs, measured retrieval, and simple, auditable workflow boundaries. Additional infrastructure such as queues, external vector stores, or cloud deployment should be added only when a real requirement justifies the added operational cost.
