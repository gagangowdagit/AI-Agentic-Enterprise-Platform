# AI-Agentic-Enterprise-Platform

Final local-demo/portfolio snapshot of a full-stack enterprise workspace for project operations, document intelligence, and grounded AI assistance.

## Current Status

The repository contains a React/Vite frontend, a Java 21/Spring Boot backend, PostgreSQL/JPA/Flyway persistence, document extraction, an Ollama-backed RAG flow through Nova AI, and stateless JWT bearer authentication. This is the final local-demo/portfolio scope, not a production SaaS release. See [project-control/10-DEVELOPMENT-STATUS.md](project-control/10-DEVELOPMENT-STATUS.md) for delivered scope, known boundaries, and final verification results.

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
- Spring Security with stateless HS256 JWT bearer authentication
- React 19, TypeScript 6, Vite 8, React Router
- PostgreSQL, Spring Data JPA, Hibernate, Flyway
- Apache PDFBox, Apache POI
- Ollama with `nomic-embed-text` and `llama3.2:3b`
- JUnit, Mockito, Spring Boot Test, ESLint

MongoDB, Redis, Qdrant/pgvector, RabbitMQ, WebSockets, Docker, CI/CD, cloud, and Kubernetes are documented as future options, not current implementation claims.

## Read Me

For final implementation status, architecture, technical decisions, and project closeout, see [project-control/](project-control/), especially [10-DEVELOPMENT-STATUS.md](project-control/10-DEVELOPMENT-STATUS.md) and [03-DEV-PLAN-Agile.md](project-control/03-DEV-PLAN-Agile.md).

For a self-contained technical guide covering the product, stack, architecture, JWT behavior, and complete REST endpoint inventory, see [overview.md](overview.md).

## Run the project locally

This project needs three services running together:

- PostgreSQL on `localhost:5432`
- Ollama on `http://localhost:11434`
- Spring Boot backend on `http://localhost:8080`
- Vite frontend on `http://localhost:5173`

### 1) Prerequisites

Make sure the following are installed:

- Java 21+
- Maven or the included Maven wrapper
- Node.js 18+
- npm
- PostgreSQL
- Ollama

### 2) Start PostgreSQL

The backend configuration expects a database named `rag_db` with user `postgres` and password `root`.

Create the database in PostgreSQL if it does not already exist:

```sql
CREATE DATABASE rag_db;
```

If needed, verify the database is reachable:

```powershell
psql -h localhost -U postgres -d rag_db
```

### 3) Start Ollama and load the required models

Start the Ollama server in one terminal:

```powershell
& "$env:LOCALAPPDATA\Programs\Ollama\ollama.exe" serve
```

If `ollama` is on your PATH, this also works:

```powershell
ollama serve
```

In a second terminal, pull the embedding and LLM models used by the project:

```powershell
& "$env:LOCALAPPDATA\Programs\Ollama\ollama.exe" pull nomic-embed-text
& "$env:LOCALAPPDATA\Programs\Ollama\ollama.exe" pull llama3.2:3b
```

Or with PATH-based commands:

```powershell
ollama pull nomic-embed-text
ollama pull llama3.2:3b
```

Verify Ollama is responding:

```powershell
Invoke-WebRequest http://localhost:11434/api/tags
```

This should return a JSON list of installed models.

### 4) Start the Spring Boot backend

From the repository root:

```powershell
cd rag-backend
.\run-backend.ps1
```

On the first run, the script creates a random JWT signing key in the ignored
`rag-backend/.env` file. The same key is reused on later starts, so you do not
need to set `JWT_SECRET` manually and existing tokens are not invalidated just
because the backend restarted. Keep `.env` private and do not commit it. To use
your own key, set `JWT_SECRET` in the environment before running the script.

The backend uses the following app settings from [rag-backend/src/main/resources/application.properties](rag-backend/src/main/resources/application.properties):

- `server.port=8080`
- `spring.datasource.url=jdbc:postgresql://localhost:5432/rag_db`
- `app.jwt.access-token-lifetime=PT15M` by default; the local JWT signing key is generated once and kept in the ignored backend `.env` file.
- `ollama.api.url=http://localhost:11434`
- `ollama.embedding.model=nomic-embed-text`
- `ollama.llm.model=llama3.2:3b`

When the backend starts successfully, it should listen on `http://localhost:8080`.

### 5) Start the React frontend

Open a separate terminal and run:

```powershell
cd frontend
npm install
npm run dev
```

The development server should start on:

```text
http://localhost:5173
```

### 6) Open the app

Open the frontend URL in your browser:

```text
http://localhost:5173
```

If the app needs the backend APIs, they should be available at:

```text
http://localhost:8080/api/v1
```

### 7) Troubleshooting

- If the backend cannot connect to PostgreSQL, make sure `rag_db` exists and PostgreSQL is running.
- If Ollama errors with a connection failure, restart the server with `ollama serve` or the Windows executable path above.
- If the model is missing, run `ollama pull nomic-embed-text` and `ollama pull llama3.2:3b` again.
- If the frontend shows API errors, make sure the backend is still running on port `8080`.

### 8) Useful check commands

```powershell
Invoke-WebRequest http://localhost:11434/api/tags
Invoke-WebRequest http://localhost:8080/actuator/health
```

If you want a quick local development flow, use this sequence in separate terminals:

```powershell
# Terminal 1 - Ollama
& "$env:LOCALAPPDATA\Programs\Ollama\ollama.exe" serve

# Terminal 2 - Backend
cd rag-backend
.\run-backend.ps1

# Terminal 3 - Frontend
cd frontend
npm install
npm run dev
```