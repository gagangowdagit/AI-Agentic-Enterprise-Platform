# Future Feature Catalog

This file contains ideas that are still not implemented. It is intentionally separated from the current status documentation so the repository does not confuse future work with completed work.

## Current Status

These items are not part of the current shipped product and should be treated as backlog only.

## Highest-Value Next Features

1. Full JWT/session security and project-aware authorization.
2. Complete task lifecycle management and ownership enforcement.
3. Robust document upload, status tracking, deletion, and reprocessing.
4. Retrieval evaluation, quality checks, and better AI failure handling.
5. Durable user conversation memory and UI source attribution.
6. Production-ready CI, observability, and deployment hardening.

## Product Expansion

- workspaces and organization-level tenancy
- fine-grained project and document permission rules
- comments, approvals, and audit trails
- calendar and deadline integration
- report export and scheduled summaries
- notification center and preference management
- global search across projects, tasks, and documents

## AI Expansion

- read-only project and analytics tools
- human-confirmed task updates
- agent execution history, cancellation, and retries
- conversation summarization and retention controls
- reranking and hybrid retrieval improvements
- model/provider abstraction for future scale

## Scale and Reliability

- background job processing with idempotency
- external vector storage if retrieval scale requires it
- cache and rate-limit services if multi-instance deployment is needed
- object storage for large files and generated reports
- monitoring, backups, and disaster recovery planning

## Deployment and Governance

- environment-specific configuration and secret management
- container and dependency scanning
- staging deployment and release checks
- security review for prompt injection, data leakage, and unsafe parsing
- privacy retention and deletion policy

## Important Rule

These items remain future backlog until they are implemented, tested, and reflected in the current status files.
