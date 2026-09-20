# AI Development Instructions

This repository is an in-progress portfolio and product project. The source code and tests are the authority for implementation status; the project-control documents capture the actual shipped behavior, architecture decisions, and honest remaining work.

## Before Changing Code

1. Read the relevant module README and surrounding implementation.
2. Check the current status in `10-DEVELOPMENT-STATUS.md`.
3. Confirm whether the requested capability is already implemented, partial, or planned.
4. Prefer the smallest change that preserves source behavior and existing contracts.

Do not treat future backlog items as existing functionality. Do not claim a library, database, endpoint, integration, security control, or deployment target is implemented until it exists in code and is backed by verification.

## Engineering Rules

- Keep controllers thin and place business logic in services.
- Use DTOs and validation at API boundaries.
- Keep persistence changes in versioned Flyway migrations.
- Use environment variables for credentials, tokens, model endpoints, and deployment configuration.
- Never commit secrets or generated build output.
- Preserve public API contracts unless the task explicitly updates them.
- Add or update focused tests when behavior changes.
- Record meaningful architecture changes in `11-DECISIONS.md`.
- Update `10-DEVELOPMENT-STATUS.md` whenever the project state changes.

## AI and Data Safety

AI features must use authorized application data only. Tool execution must have explicit inputs, validation, authorization, bounded execution, and failure handling. Prompts and logs must not expose credentials or private data unnecessarily.

## Verification

Run the narrowest useful check first, then the relevant module checks. A feature is complete only when implementation, tests, documentation, and local setup instructions agree. Known unrelated failures must be recorded rather than hidden.

## Source-of-Truth Order

1. Compiled source and tests
2. Database migrations and runtime configuration
3. Current architecture and decisions
4. Requirements and development status
5. Backlog and future proposals

Project-control files must be updated when they conflict with the implementation.