# API Design

## Contract

The backend uses JSON REST APIs under `/api/v1`. Controllers validate request DTOs and return a consistent `ApiResponse` envelope where the endpoint contract defines one. Errors are normalized by the global exception handler.

## Implemented Surface

The exact route inventory should be confirmed from controller annotations. The current API groups include:

- `/api/v1/system` for service status/health.
- `/api/v1/auth` and `/api/v1/users` for current registration/login/user flows.
- `/api/v1/projects` for project lifecycle, project details, teams, documents, timelines, and project insights.
- `/api/v1/departments` and team/member routes for organizational data.
- `/api/v1/documents` for document operations and processing.
- `/api/v1/analytics` for company/project analytics.
- `/api/v1/rag/query` for project-scoped grounded question answering.

## RAG Query Contract

```http
POST /api/v1/rag/query
Content-Type: application/json
```

```json
{
  "projectId": "1",
  "query": "What is the project status?",
  "topK": 3
}
```

The response contains the query, generated answer, retrieved context, and de-duplicated source metadata when available. `projectId` may use the current global scope behavior where supported.

## Error Contract

Validation and application failures should provide an HTTP status plus a stable error code/message. Internal exception details must not leak secrets, database credentials, prompt contents, or stack traces in production.

## Planned Contract Work

- Publish generated OpenAPI as the canonical route/schema reference.
- Complete DTO coverage and pagination conventions.
- Add authentication and authorization requirements per route.
- Add idempotency keys for upload and mutating AI actions.
- Add request correlation IDs and rate-limit headers.
- Version breaking changes rather than silently changing response shapes.
