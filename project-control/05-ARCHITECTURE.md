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

- project, team, department, and document management
- meeting scheduling and lifecycle actions
- transcript upload and summary generation
- document extraction and project knowledge retrieval
- AI answer generation using local Ollama models

Global exception handling and API response wrappers keep success and failure behavior consistent across modules.

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

## Current Boundaries

The architecture is intentionally monolithic for the current project state. It is built for a local enterprise demo and portfolio deployment, not for a multi-service production platform.

## Architecture Principles

Keep domain behavior independent from model infrastructure. Prefer explicit service interfaces, validated DTOs, measured retrieval, and simple, auditable workflow boundaries. Additional infrastructure such as queues, external vector stores, or cloud deployment should be added only when a real requirement justifies the added operational cost.
