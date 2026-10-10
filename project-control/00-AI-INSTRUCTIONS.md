# AI Development Instructions

This repository is documented as a closed local-demo/portfolio project snapshot. The source code and tests remain the authority for implementation status; project-control documents record delivered behavior, final verification, architectural decisions, and explicit scope boundaries. This file is retained as engineering guidance if the repository is ever changed, not as an active roadmap.

## Before Changing Code

1. Read the relevant module README and surrounding implementation.
2. Check the final status in `10-DEVELOPMENT-STATUS.md`.
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
- If a separately scoped future change is made, record meaningful architecture changes in `11-DECISIONS.md` and update `10-DEVELOPMENT-STATUS.md`.

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

Project-control files must agree with the implementation. No feature backlog or active project phase is maintained for this final snapshot.