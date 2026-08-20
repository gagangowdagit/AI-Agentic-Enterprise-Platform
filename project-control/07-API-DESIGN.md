# API Design

## 1. API Standards

The backend will expose REST APIs using JSON.

Base URL:

```text
/api/v1
```

Use standard HTTP methods:

```text
GET     → Read
POST    → Create
PUT     → Update
PATCH   → Partial Update
DELETE  → Delete
```

---

## 2. Authentication APIs

```text
POST   /api/v1/auth/register
POST   /api/v1/auth/login
POST   /api/v1/auth/refresh
POST   /api/v1/auth/logout
GET    /api/v1/auth/me
```

---

## 3. User APIs

```text
GET    /api/v1/users
GET    /api/v1/users/{id}
POST   /api/v1/users
PUT    /api/v1/users/{id}
DELETE /api/v1/users/{id}
```

---

## 4. Project APIs

```text
GET    /api/v1/projects
GET    /api/v1/projects/{id}
POST   /api/v1/projects
PUT    /api/v1/projects/{id}
DELETE /api/v1/projects/{id}

POST   /api/v1/projects/{id}/members
DELETE /api/v1/projects/{id}/members/{userId}
```

---

## 5. Task APIs

```text
GET    /api/v1/tasks
GET    /api/v1/tasks/{id}
POST   /api/v1/tasks
PUT    /api/v1/tasks/{id}
DELETE /api/v1/tasks/{id}

PATCH  /api/v1/tasks/{id}/status
PATCH  /api/v1/tasks/{id}/assign
```

---

## 6. Document APIs

```text
GET    /api/v1/documents
GET    /api/v1/documents/{id}
POST   /api/v1/documents/upload
DELETE /api/v1/documents/{id}

GET    /api/v1/documents/{id}/status
```

---

## 7. AI APIs

```text
POST   /api/v1/ai/chat
GET    /api/v1/ai/conversations
GET    /api/v1/ai/conversations/{id}
DELETE /api/v1/ai/conversations/{id}
```

---

## 8. RAG APIs

```text
POST   /api/v1/rag/search
POST   /api/v1/rag/query
```

RAG responses should include source information where applicable.

---

## 9. Agent APIs

```text
POST   /api/v1/agents/execute
GET    /api/v1/agents/executions
GET    /api/v1/agents/executions/{id}
POST   /api/v1/agents/executions/{id}/cancel
```

---

## 10. Analytics APIs

```text
GET    /api/v1/analytics/projects/{id}
GET    /api/v1/analytics/tasks
GET    /api/v1/analytics/ai
```

---

## 11. Report APIs

```text
POST   /api/v1/reports/project
POST   /api/v1/reports/task
POST   /api/v1/reports/analytics

GET    /api/v1/reports
GET    /api/v1/reports/{id}
```

---

## 12. Notification APIs

```text
GET    /api/v1/notifications
PATCH  /api/v1/notifications/{id}/read
PATCH  /api/v1/notifications/read-all
```

---

## 13. Response Structure

Successful responses should follow a consistent structure.

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {},
  "timestamp": "..."
}
```

Error responses:

```json
{
  "success": false,
  "message": "Resource not found",
  "errorCode": "RESOURCE_NOT_FOUND",
  "timestamp": "..."
}
```

---

## 14. API Security

* Protected APIs require authentication.
* Authorization must be checked before accessing protected resources.
* User input must be validated.
* Sensitive information must not be returned unnecessarily.
* AI tools must respect the same authorization rules as normal APIs.

---

## 15. API Documentation

All public APIs should be documented using:

* OpenAPI
* Swagger UI

API changes must be reflected in the documentation.
