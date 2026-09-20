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

## Current Implementation Notes

The current implementation uses a relational model for core project data and the embedding pipeline. This is sufficient for the application scope and keeps the architecture simple and auditable.

## Future Storage Decisions

No additional database technology is currently required by the project. If scale or performance ever demands it, future changes should be introduced only after measuring the workload and documenting the tradeoff in `11-DECISIONS.md`.
