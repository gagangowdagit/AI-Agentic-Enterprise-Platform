# AI Agentic Enterprise Platform

## Purpose

This repository contains an in-progress full-stack enterprise workspace that combines project operations with document intelligence and grounded AI assistance. It is both a usable product foundation and a portfolio project demonstrating API design, relational persistence, document processing, retrieval-augmented generation, and a path toward controlled agent automation.

## Current Product Slice

The working slice today includes:

- React/Vite pages for login, registration, home, projects, project details, departments, teams, documents, analytics, and Nova AI.
- Spring Boot REST APIs under `/api/v1` for health, authentication/user flows, projects, teams, departments, documents, analytics, timelines, insights, and RAG queries.
- PostgreSQL persistence through Spring Data JPA and Flyway migrations.
- PDF, DOCX, and text document extraction and chunk persistence.
- Ollama-backed embeddings with `nomic-embed-text` and grounded answer generation with `llama3.2:3b`.
- Project-scoped and global retrieval using normalized embeddings and top-K similarity search.
- Standard API responses, validation, exception handling, OpenAPI support, and a growing JUnit/Mockito test suite.

## Product Vision

The long-term product is an AI operations assistant for project teams. It should help people find trusted project knowledge, understand delivery health, inspect work, and eventually perform explicitly authorized actions through auditable tools. The AI is an application capability, not a replacement for authorization or business rules.

## Audience

The initial audience is project managers, team members, administrators, and organizations that need a private knowledge assistant over project documents. The repository also demonstrates engineering depth for recruiters: layered Java services, a typed frontend, persistence, migrations, document ingestion, local model integration, tests, and an incremental delivery process.

## Current Boundary

This is not yet a production SaaS platform. JWT enforcement, fine-grained authorization, conversation persistence, streaming, asynchronous jobs, external vector infrastructure, observability, deployment automation, and comprehensive end-to-end coverage remain future work. See `10-DEVELOPMENT-STATUS.md` and `03-DEV-PLAN-Agile.md` for the authoritative status and backlog.
