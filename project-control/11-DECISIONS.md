# Technical Decisions

This file records important architectural and technical decisions made during development.

## Decision Format

```text
Decision ID:
Date:
Topic:
Decision:
Reason:
Impact:
Status:
```

---

## Decisions

### DECISION-001

```text
Decision ID: DECISION-001
Date: 2026-08-20
Topic: Primary Backend Framework
Decision: Spring Boot
Reason: Strong enterprise ecosystem and suitable for REST APIs, security, JPA/Hibernate, asynchronous processing, and scalable backend development.
Impact: Backend development will be based on Spring Boot.
Status: ACCEPTED
```

### DECISION-002

```text
Decision ID: DECISION-002
Date: 2026-08-20
Topic: Frontend Framework
Decision: React + TypeScript + Vite
Reason: Component-based architecture and strong ecosystem for modern web applications.
Impact: Frontend will use React and TypeScript.
Status: ACCEPTED
```

### DECISION-003

```text
Decision ID: DECISION-003
Date: 2026-08-20
Topic: Primary Relational Database
Decision: PostgreSQL
Reason: Reliable relational database suitable for transactional enterprise data.
Impact: Core business entities will use PostgreSQL.
Status: ACCEPTED
```

### DECISION-004

```text
Decision ID: DECISION-004
Date: 2026-08-20
Topic: NoSQL Database
Decision: MongoDB
Reason: Flexible document storage suitable for conversations, AI execution data, and other evolving AI-related structures.
Impact: Selected AI-related data will use MongoDB.
Status: ACCEPTED
```

### DECISION-005

```text
Decision ID: DECISION-005
Date: 2026-08-20
Topic: Cache
Decision: Redis
Reason: Suitable for caching, temporary state, rate limiting, and distributed locking.
Impact: Redis will be used for appropriate temporary and frequently accessed data.
Status: ACCEPTED
```

### DECISION-006

```text
Decision ID: DECISION-006
Date: 2026-08-20
Topic: Vector Database
Decision: Qdrant
Reason: Suitable for vector storage and semantic retrieval required by the RAG system.
Impact: Qdrant will initially handle document vector search.
Status: ACCEPTED
```

### DECISION-007

```text
Decision ID: DECISION-007
Date: 2026-08-20
Topic: Message Broker
Decision: RabbitMQ
Reason: Provides reliable asynchronous messaging and event-driven processing.
Impact: Background jobs and selected domain events will use RabbitMQ.
Status: ACCEPTED
```

### DECISION-008

```text
Decision ID: DECISION-008
Date: 2026-08-20
Topic: ORM
Decision: Hibernate + JPA
Reason: Provides enterprise-grade ORM and integrates naturally with Spring Boot.
Impact: PostgreSQL persistence will use Hibernate/JPA.
Status: ACCEPTED
```

### DECISION-009

```text
Decision ID: DECISION-009
Date: 2026-08-20
Topic: AI Integration
Decision: Spring AI
Reason: Provides an AI integration layer that fits naturally into the Spring Boot backend.
Impact: AI functionality will be integrated through a dedicated AI layer.
Status: ACCEPTED
```

### DECISION-010

```text
Decision ID: DECISION-010
Date: 2026-08-20
Topic: Version Control
Decision: Git + GitHub
Reason: Required for version control, collaboration, stable checkpoints, and rollback.
Impact: All development will be tracked through Git.
Status: ACCEPTED
```

---

## Decision Change Policy

An accepted decision must not be changed silently.

When a decision changes:

1. Create a new decision entry.
2. Explain why the previous decision is being changed.
3. Identify the affected components.
4. Update the relevant project-control document.
5. Mark the previous decision as `SUPERSEDED`.

Example:

```text
DECISION-011

Topic: Vector Database

Decision: Replace Qdrant with another vector database.

Replaces: DECISION-006

Reason:
...

Impact:
...

Status: ACCEPTED
```
