# System Architecture

## 1. Architecture Overview

The system will follow a modular full-stack architecture.

```text
React Frontend
      │
      ▼
Spring Boot Backend
      │
      ├── Authentication & Authorization
      ├── Project & Task Management
      ├── Document Management
      ├── AI Orchestration
      ├── RAG Services
      ├── Notification Services
      └── Analytics
      │
      ├──────────────┬──────────────┐
      ▼              ▼              ▼
PostgreSQL       MongoDB         Redis
      │              │
      └───────┬──────┘
              ▼
         AI Platform
              │
       ┌──────┼──────┐
       ▼      ▼      ▼
      RAG    Agents  Tools
       │      │      │
       ▼      ▼      ▼
   Qdrant    LLM   App Services
```

---

## 2. Frontend Architecture

```text
frontend/
└── src/
    ├── components/
    ├── pages/
    ├── layouts/
    ├── services/
    ├── hooks/
    ├── context/
    ├── types/
    ├── utils/
    └── routes/
```

React communicates with Spring Boot through REST APIs and WebSocket connections.

---

## 3. Backend Architecture

The backend will follow a layered architecture.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Additional layers:

```text
Controller
    ↓
DTO
    ↓
Service
    ↓
Domain / Entity
    ↓
Repository
```

The backend will use interfaces and dependency injection to maintain loose coupling.

---

## 4. AI Architecture

The AI layer will be separated from normal business logic.

```text
User Request
     ↓
AI Orchestrator
     ↓
Supervisor Agent
     │
     ├── RAG Agent
     ├── Database Agent
     ├── Analytics Agent
     ├── Task Agent
     ├── Report Agent
     └── Notification Agent
     │
     ▼
Tool Execution
     │
     ▼
Result
     │
     ▼
AI Response
```

Agents will only access authorized tools.

---

## 5. RAG Architecture

```text
Document
   ↓
Text Extraction
   ↓
Chunking
   ↓
Embedding
   ↓
Qdrant
   ↓
Semantic Search
   ↓
Relevant Chunks
   ↓
LLM
   ↓
Response + Sources
```

---

## 6. Data Architecture

### PostgreSQL

Used for transactional relational data:

* Users
* Roles
* Projects
* Tasks
* Permissions
* Notifications

### MongoDB

Used for flexible AI-related data:

* Conversations
* Chat history
* Agent executions
* AI-generated reports

### Redis

Used for:

* Caching
* Temporary state
* Rate limiting
* Distributed locks

### Qdrant

Used for:

* Document embeddings
* Vector search
* RAG retrieval

---

## 7. Asynchronous Architecture

Long-running operations should not block normal API requests.

```text
API Request
    ↓
RabbitMQ
    ↓
Background Worker
    ↓
Processing
    ↓
Database / Vector DB
    ↓
WebSocket Update
    ↓
React UI
```

Java concurrency and Spring asynchronous processing will be used where appropriate.

---

## 8. Real-Time Architecture

```text
Spring Boot
    │
    ▼
WebSocket
    │
    ▼
React
```

Used for:

* Agent execution status
* Document processing status
* Notifications
* Long-running task updates

---

## 9. Deployment Architecture

```text
GitHub
   ↓
Jenkins
   ↓
Docker Build
   ↓
AWS ECR
   ↓
Kubernetes / EKS
   ↓
Application
```

Production infrastructure will use appropriate AWS services such as:

* EKS
* RDS
* S3
* Load Balancer
* CloudWatch
* IAM

---

## 10. Architecture Principles

The system should follow:

* Modular design
* Separation of concerns
* SOLID principles
* Loose coupling
* Secure access control
* Testability
* Observability
* Scalability
* Maintainability

Architecture changes must be documented in `11-DECISIONS.md`.
