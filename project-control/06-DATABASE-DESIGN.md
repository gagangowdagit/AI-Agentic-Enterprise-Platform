# Database Design

## Current Database

PostgreSQL is the only active application database. Spring Data JPA maps domain entities and Flyway owns schema evolution. The runtime uses `spring.jpa.hibernate.ddl-auto=none` so migrations, not Hibernate startup mutation, define the schema.

## Current Relational Areas

The repository contains relational models and repositories for users, projects, departments, teams/memberships, documents, document chunks, chunk embeddings, tasks, notifications, timelines, and related analytics/AI data. Exact columns and constraints are defined by migrations under `rag-backend/src/main/resources/db/migration` and must be treated as the schema source of truth.

Important current RAG tables:

```text
documents
  -> document_chunks
       -> chunk_embeddings
```

Document file name is carried into retrieval results as response metadata rather than persisted directly on each transient chunk result.

## Data Rules

- Use foreign keys, not duplicated identifiers, for ownership relationships.
- Add indexes based on measured query patterns.
- Use transactions for multi-table mutations and embedding replacement.
- Store model name, embedding dimensions, and processing version with embeddings before production scale.
- Keep uploaded file bytes separate from relational metadata when object storage is introduced.
- Do not store passwords, tokens, or provider secrets in plaintext.
- Use retention and deletion rules for conversations, files, and AI execution records.

## Future Data Stores

| Store | Status | Candidate responsibility |
|---|---|---|
| PostgreSQL | Implemented | Transactional entities and current RAG metadata/embeddings |
| pgvector or Qdrant | Planned | Vector search if application-side search no longer meets scale/latency targets |
| Redis | Planned | Cache, rate limits, locks, short-lived job state |
| MongoDB | Optional | Conversation/execution documents only if relational storage becomes a poor fit |
| Object storage | Planned | Durable document bytes and generated reports |

No additional store is approved merely because it appears in a future diagram. The choice must be recorded in `11-DECISIONS.md` with migration and operational implications.
