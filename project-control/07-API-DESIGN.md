# API Design

## Contract

The backend uses JSON REST APIs under `/api/v1`. Controllers validate request DTOs and return consistent success/error responses through the application response layer and the global exception handler.

## Authentication Contract

`POST /api/v1/auth/login` is public and accepts the registered email and password. A successful response includes `accessToken`, `tokenType` (`Bearer`), `expiresIn`, and `expiresAt`. Send the token on protected requests as:

```http
Authorization: Bearer <accessToken>
```

The backend validates the HS256 signature and token expiry and uses stateless sessions. Access-token lifetime defaults to 15 minutes. Login, health checks, `GET /api/v1/departments`, `POST /api/v1/employees`, and CORS preflight requests are public exceptions in the current security configuration. Other routes require a valid bearer token.

Authentication does not provide fine-grained role or project/resource ownership authorization. There are no refresh-token, revocation, or logout endpoints.

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
- `/api/v1/projects/{projectId}/task-summary` for project task aggregates
- `/api/v1/notifications` for user notification retrieval and marking notifications read

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

## Final API Boundaries

The task summary is not a general task CRUD API. Notification routes and other authenticated endpoints do not yet enforce a per-user or per-project ownership policy. Refresh tokens, revocation, fine-grained RBAC, pagination standards, rate limiting, and mutation idempotency are outside this final delivery. These boundaries are not an active API backlog for this closed project scope.
