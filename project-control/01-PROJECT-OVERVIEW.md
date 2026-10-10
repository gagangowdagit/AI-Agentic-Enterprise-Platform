# AI Agentic Enterprise Platform

Final portfolio/local-demo snapshot of a full-stack enterprise workspace for project operations, document intelligence, and grounded AI assistance.

## Purpose

This repository contains a completed local-demo/portfolio project combining project operations with document intelligence and AI-assisted workflows. The agreed delivery scope is closed. It demonstrates API design, relational persistence, document processing, retrieval-augmented generation, and bounded agent workflows.

## Current Status

The repository contains a working React/Vite frontend, a Java 21/Spring Boot backend, PostgreSQL/JPA/Flyway persistence, document extraction, an Ollama-backed RAG flow through Nova AI, and stateless JWT bearer authentication. The final state and tested limitations are recorded in `10-DEVELOPMENT-STATUS.md`. This project is not a production SaaS release.

## Current Product Slice

The working slice today includes:

- React/Vite pages for login, registration, home, projects, project details, departments, teams, documents, analytics, and Nova AI.
- Spring Boot REST APIs under `/api/v1` for health, authentication/user flows, projects, teams, departments, documents, analytics, timelines, insights, and RAG queries.
- PostgreSQL persistence through Spring Data JPA and Flyway migrations.
- PDF, DOCX, and text document extraction and chunk persistence.
- Ollama-backed embeddings with `nomic-embed-text` and grounded answer generation with `llama3.2:3b`.
- Project-scoped and global retrieval using normalized embeddings and top-K similarity search.
- Standard API responses, validation, exception handling, OpenAPI support, and a growing JUnit/Mockito test suite.
- Meeting management flows with scheduling, participants, agenda items, Google Meet links, AI transcript handling, and AI-generated summaries.
- Backend task persistence, project task summaries, agent task tools, and user notifications; a full task-management UI is not yet available.

## Product Vision

The long-term product is an AI operations assistant for project teams. It should help people find trusted project knowledge, understand delivery health, inspect work, and eventually perform explicitly authorized actions through auditable tools. The AI is an application capability, not a replacement for authorization or business rules.

## Audience

The initial audience is project managers, team members, administrators, and organizations that need a private knowledge assistant over project documents. The repository also demonstrates engineering depth for recruiters: layered Java services, a typed frontend, persistence, migrations, document ingestion, local model integration, tests, and an incremental delivery process.

## Repository Structure

- `project-control/`: planning, architecture, requirements, and status tracking
- `rag-backend/`: Java Spring Boot backend
- `frontend/`: React application workspace
- `ai/`: AI agents and prompt logic
- `docs/`: product and design documents
- `infrastructure/`: deployment and environment resources
- `devops/`: future DevOps, CI/CD, deployment, and infrastructure planning
- `tests/`: validation and QA assets

## Active Stack

- Java 21, Spring Boot 4.1.1, Spring MVC, Maven
- React 19, TypeScript 6, Vite 8, React Router
- PostgreSQL, Spring Data JPA, Hibernate, Flyway
- Apache PDFBox, Apache POI
- Ollama with `nomic-embed-text` and `llama3.2:3b`
- JUnit, Mockito, Spring Boot Test, ESLint

MongoDB, Redis, Qdrant/pgvector, RabbitMQ, WebSockets, Docker, CI/CD, cloud, and Kubernetes are documented as future options, not current implementation claims.

## Current Boundary

This is a completed local-demo/portfolio deliverable, not a production SaaS platform. JWT authentication is implemented; fine-grained authorization, conversation persistence, streaming, asynchronous jobs, external vector infrastructure, observability, and deployment automation are explicitly outside the delivered scope. The final status and verification record is in `10-DEVELOPMENT-STATUS.md`; the delivery plan is closed in `03-DEV-PLAN-Agile.md`.

## Read Me

For the final implementation status, architecture, and decisions, see [10-DEVELOPMENT-STATUS.md](10-DEVELOPMENT-STATUS.md), [03-DEV-PLAN-Agile.md](03-DEV-PLAN-Agile.md), and [11-DECISIONS.md](11-DECISIONS.md).

## Local Setup

### Start the backend

```powershell
cd rag-backend
.\run-backend.ps1
```

On first start, this script creates and saves a random JWT signing key in the
ignored `rag-backend/.env` file, then reuses it on future starts. Keep that file
private and out of version control.

The RAG backend requires Ollama at `http://localhost:11434`. On Windows, start it in a separate terminal:

```powershell
& "$env:LOCALAPPDATA\Programs\Ollama\ollama.exe" serve
& "$env:LOCALAPPDATA\Programs\Ollama\ollama.exe" pull nomic-embed-text
& "$env:LOCALAPPDATA\Programs\Ollama\ollama.exe" pull llama3.2:3b
```

Verify the service before using Nova AI:

```powershell
Invoke-WebRequest http://localhost:11434/api/tags
```

### Start the frontend

```powershell
cd frontend
npm run dev
```

## Developer Notes

- The project follows a layered architecture: controller → service → repository → JPA entity.
- The backend exposes REST APIs for project, document, department, team, meeting, analytics, and AI-driven workflows.
- The frontend is a modular Vite React app with page-level features and separate API service modules.
- Document processing and AI retrieval are implemented around local model execution via Ollama.
- The work is intentionally incremental: feature slices are released in stages with clear documentation and known boundaries.

## Key Functional Areas

- Project operations and management
- Team and department management
- Document upload and knowledge ingestion
- AI-powered project insights and grounded responses
- Meeting lifecycle with planner, participants, agenda, and meeting links
- Analytics and reporting views
- AI-driven project and meeting intelligence

## Architecture Summary

This project is structured as a practical enterprise demo with a monolithic Spring Boot backend and a React frontend. The backend is responsible for persistence, API delivery, document processing, and AI orchestration. The frontend is responsible for the user experience, content rendering, form flows, and API consumption. The repository is intentionally designed to be easy to understand and extend without requiring cloud infrastructure for local development.

## Project Closeout

The planned local-demo/portfolio scope is complete and there is no active next phase. Production-scale features are excluded from this final snapshot rather than presented as delivered or as a pending roadmap. See the final status document for the verification results and explicit boundaries.

## Reference Documents

- [project-control/00-AI-INSTRUCTIONS.md](00-AI-INSTRUCTIONS.md)
- [project-control/02-TECH-STACK.md](02-TECH-STACK.md)
- [project-control/03-DEV-PLAN-Agile.md](03-DEV-PLAN-Agile.md)
- [project-control/04-REQUIREMENTS.md](04-REQUIREMENTS.md)
- [project-control/05-ARCHITECTURE.md](05-ARCHITECTURE.md)
- [project-control/06-DATABASE-DESIGN.md](06-DATABASE-DESIGN.md)
- [project-control/07-API-DESIGN.md](07-API-DESIGN.md)
- [project-control/08-AI-RAG-AGENT-DESIGN.md](08-AI-RAG-AGENT-DESIGN.md)
- [project-control/09-GIT-STRATEGY.md](09-GIT-STRATEGY.md)
- [project-control/10-DEVELOPMENT-STATUS.md](10-DEVELOPMENT-STATUS.md)
- [project-control/11-DECISIONS.md](11-DECISIONS.md)

The project-control folder records the final implementation scope, architecture, and decisions for this repository snapshot.
