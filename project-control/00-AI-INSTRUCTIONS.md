# AI Development Instructions

This repository is an in-progress portfolio project. The source code is the authority for implementation status; project-control documents describe intent, constraints, and the next work.

## Before Changing Code

1. Read the relevant module README and nearby implementation.
2. Check `10-DEVELOPMENT-STATUS.md` for the current phase.
3. Confirm whether the requested capability is implemented, partial, or planned.
4. Prefer the smallest change that preserves existing behavior.

Do not treat future roadmap items as existing functionality. Do not claim a library, database, endpoint, security control, or deployment target is implemented until it exists in source and has been verified.

## Engineering Rules

- Keep controllers thin and business logic in services.
- Use DTOs and validation at API boundaries.
- Keep persistence changes in versioned Flyway migrations.
- Use environment variables for credentials, tokens, model endpoints, and deployment configuration.
- Never commit secrets or generated build output.
- Preserve public API contracts unless the task explicitly changes them.
- Add or update focused tests for behavior changes.
- Record meaningful architecture changes in `11-DECISIONS.md`.
- Update `10-DEVELOPMENT-STATUS.md` and `12-CHANGELOG.md` when work changes the project state.

## AI and Data Safety

AI features must use authorized application data only. Tool execution must have explicit inputs, validation, authorization, bounded execution, and failure handling. Prompts and logs must not expose credentials or private data unnecessarily.

## Verification

Run the narrowest useful check first, then the relevant module checks. A feature is complete only when the implementation, tests, documentation, and local setup instructions agree. Known unrelated failures must be recorded rather than hidden.

## Source-of-Truth Order

1. Compiled source and tests
2. Database migrations and runtime configuration
3. Current architecture and decisions
4. Requirements and development status
5. Roadmap and future proposals

Project-control files must be revised when they conflict with the implementation.