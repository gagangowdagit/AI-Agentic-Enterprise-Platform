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
    Services + AI services
          |
    Repositories / processors
       |              |
       v              v
 PostgreSQL        Ollama
 (JPA/Flyway)      (embed/generate)
```

## Frontend

`frontend/src` is organized around routed pages, reusable components, and API service modules. Pages currently cover authentication, home, projects, project details, departments, teams, documents, analytics, and Nova AI. The frontend calls the backend through REST and currently keeps API configuration in service/page code; environment-based configuration is a near-term improvement.

## Backend

The backend uses a conventional layered Spring architecture:

```text
Controller -> DTO/validation -> Service -> Repository -> PostgreSQL
                         |
                         +-> document processors
                         +-> embedding/retrieval services
                         +-> Ollama LLM service
```

Dependency injection is used for service boundaries such as `EmbeddingService`, `LlmService`, and `RagService`. Global exception handling and API response wrappers keep errors and success responses consistent.

## Current RAG Flow

```text
RAG query
  -> embed query with Ollama /api/embed
  -> load authorized project documents/chunks
  -> calculate application-side similarity and select top-K
  -> build grounded prompt
  -> generate with Ollama /api/generate
  -> return answer, context, and source metadata
```

Embeddings currently live in the relational model as serialized values and are compared in application code. This is a deliberately simple local implementation, not yet a high-scale vector architecture.

## Future Boundaries

- Add Spring Security at the API boundary before exposing the application beyond local development.
- Add an object-storage abstraction for uploaded files.
- Add a job boundary for long-running ingestion and agent executions.
- Add a vector-store adapter only if retrieval volume justifies pgvector or Qdrant.
- Add event delivery, WebSocket/SSE, or a message broker only for a demonstrated asynchronous workflow.

## Architecture Principles

Keep domain behavior independent from Ollama and infrastructure details. Prefer explicit service interfaces, validated DTOs, least privilege, idempotent processing, measurable quality, and small deployable changes. A future component belongs in the architecture only after its failure mode, ownership, testing strategy, and operational cost are understood.
