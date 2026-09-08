# AI-Agentic-Enterprise-Platform

An in-progress full-stack enterprise workspace for project operations, document intelligence, and grounded AI assistance.

## Current Status

The repository contains a working React/Vite frontend, a Java 21/Spring Boot backend, PostgreSQL/JPA/Flyway persistence, document extraction, and an Ollama-backed RAG flow through Nova AI. It is a pre-release development project: security hardening, durable conversations, async processing, observability, deployment, and production-scale infrastructure are still being built.

## Structure

- project-control/: planning, architecture, requirements, and status tracking
- rag-backend/: Java Spring Boot backend
- frontend/: React application workspace
- ai/: AI agents and prompt logic
- docs/: product and design documents
- infrastructure/: deployment and environment resources
- devops/: future DevOps, CI/CD, deployment, and infrastructure planning
- tests/: validation and QA assets

## Active Stack

- Java 21, Spring Boot 4.1.1, Spring MVC, Maven
- React 19, TypeScript 6, Vite 8, React Router
- PostgreSQL, Spring Data JPA, Hibernate, Flyway
- Apache PDFBox, Apache POI
- Ollama with `nomic-embed-text` and `llama3.2:3b`
- JUnit, Mockito, Spring Boot Test, ESLint

MongoDB, Redis, Qdrant/pgvector, RabbitMQ, WebSockets, Docker, CI/CD, cloud, and Kubernetes are documented as future options, not current implementation claims.

## Read Me

For implementation status, architecture, technical decisions, and the complete future roadmap, see [project-control/](project-control/), especially [10-DEVELOPMENT-STATUS.md](project-control/10-DEVELOPMENT-STATUS.md) and [03-DEV-PLAN-Agile.md](project-control/03-DEV-PLAN-Agile.md).


To run the backend:

```powershell
cd rag-backend
.\mvnw.cmd spring-boot:run
```

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

To run the frontend:

```powershell
cd frontend
npm run dev
```