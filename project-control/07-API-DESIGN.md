# API Design

## Contract

The backend uses JSON REST APIs under `/api/v1`. Controllers validate request DTOs and return consistent success/error responses through the application response layer and the global exception handler.

## Current API Groups

The implemented API surface includes:

- `/api/v1/system` for status and health checks
- `/api/v1/auth` and `/api/v1/users` for register/login/user flows
- `/api/v1/projects` for project lifecycle, team access, documents, timelines, and insights
- `/api/v1/departments` and related routes for organization structure
- `/api/v1/documents` for upload and processing operations
- `/api/v1/analytics` for project and company analytics
- `/api/v1/rag` for project-scoped grounded question answering
- `/api/v1/meetings` for meeting management and AI summary endpoints

## Core Meeting Contract

```http
POST /api/v1/meetings
Content-Type: application/json
```

```json
{
  "title": "Sprint Review",
  "description": "Weekly product review",
  "projectId": "1",
  "scheduledAt": "2026-09-20T10:00:00",
  "durationMinutes": 60,
  "googleMeetLink": "https://meet.google.com/abc-defg-hij",
  "agenda": "Sprint review; risks; next actions",
  "participantIds": [1, 2, 3]
}
```

Meeting flows also support update, cancel, delete, transcript upload, and summary regeneration.

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

The response contains the generated answer and the supporting retrieved context from the selected project documents.

## Error Contract

Validation and application failures return HTTP status codes along with an error payload rather than leaking backend internals. Internal exceptions should not expose model or database details in the public API.

## Planned API Work

The next API work is focused on production hardening rather than adding new feature types:

- strict authentication and authorization checks per route
- richer request validation and rate-limit behavior
- consistent pagination conventions for large collections
- idempotency for mutating AI and document operations
- correlation IDs and safe operational logging

This is a hardening plan, not a list of unimplemented product features.
