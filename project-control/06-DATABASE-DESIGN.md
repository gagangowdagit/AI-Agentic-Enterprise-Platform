# Database Design

## Current Database

PostgreSQL is the active application database for the current implementation. Spring Data JPA maps the domain entities and Flyway owns schema evolution. The app is designed so the migration scripts remain the schema source of truth.

## Core Relational Areas

The current schema supports:

- users and authentication state
- departments and employees
- projects and project-related metadata
- task and team relationships
- project documents and document processing metadata
- document chunks and stored embeddings
- meetings, participants, agendas, and transcript metadata
- analytics and AI-generated meeting summary records

Important current data relationships:

```text
documents
  -> document_chunks
       -> chunk_embeddings

projects
  -> meetings
  -> documents
  -> team records
```

## Current Design Principles

- Use foreign keys to model ownership and relationships.
- Keep business metadata separate from generated AI data.
- Persist document and meeting summary metadata in the relational model.
- Keep model and embedding metadata explicit for debugging and traceability.
- Do not store sensitive credentials in application data tables.
- Passwords are stored as BCrypt hashes for newly registered accounts. Legacy plaintext credentials may still exist and are supported by a compatibility fallback; no JWT signing key or access token is stored in PostgreSQL.

## Current Implementation Notes

The current implementation uses a relational model for core project data and the embedding pipeline. This is sufficient for the application scope and keeps the architecture simple and auditable.

Employee identity currently has a single `role` field. It is returned by login and used for frontend role-aware presentation; there is no distinct persisted application access role or role-to-authority mapping. It is not a server-side authorization control.

Task records, project memories, and agent execution records are also persisted. Task behavior is currently backend/agent-facing and does not yet provide a complete user-facing task workflow.

## Final Storage Scope

JWT access tokens are stateless and short-lived; the database stores neither tokens nor signing keys. No separate vector database, cache, queue, or conversation-history store is part of this final delivery.
