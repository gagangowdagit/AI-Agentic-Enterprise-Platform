# Future Feature Catalog

These are candidates, not commitments. Priority should follow user value, security, measured pain, and the smallest architecture that solves the problem.

## Highest-Value Next Features

1. JWT/session security and project-aware authorization.
2. Complete task management and ownership rules.
3. Reliable document upload, processing status, deletion, and reprocessing.
4. RAG evaluation set, thresholds, sources UI, model health, and better errors.
5. Durable Nova AI conversations with privacy/retention controls.
6. Clean CI baseline and PostgreSQL/Ollama local environment.

## Product Expansion

- Workspaces and organization tenancy.
- Fine-grained project/document permissions.
- Comments, activity history, approvals, and audit logs.
- Calendar/deadline integrations and workload views.
- Report export and scheduled project summaries.
- Notification center, email delivery, and configurable preferences.
- Search across projects, tasks, and documents with filters.

## AI Expansion

- Read-only project/task/analytics tools.
- Human-confirmed task creation and updates.
- Agent execution history, cancellation, retries, and audit trail.
- Conversation summarization and user preferences.
- Retrieval reranking, hybrid keyword/vector search, and document version awareness.
- Model/provider abstraction for cloud or self-hosted alternatives.


## Scale and Reliability

- Background ingestion jobs with idempotency and dead-letter handling.
- pgvector or Qdrant when measured retrieval scale requires it.
- Redis for rate limits/cache/short-lived locks when multi-instance deployment needs it.
- Object storage for files and generated reports.
- Structured logging, metrics, tracing, readiness probes, backups, and disaster recovery.
- Docker Compose for local dependencies and GitHub Actions for CI.

## Deployment and Governance

- Environment-specific configuration and secret management.
- Container image scanning and dependency updates.
- Staging deployment before production.
- Threat modeling for prompt injection, data leakage, SSRF, unsafe file parsing, and tool abuse.
- Data retention, deletion, export, and privacy documentation.
- Load, recovery, and security testing.

## Ideas to Evaluate Carefully

Multi-agent collaboration, Kubernetes/EKS, RabbitMQ, MongoDB, and a large cloud architecture are possible future directions, but they should not be added for resume keywords alone. A smaller auditable workflow is often the stronger product and engineering choice.
