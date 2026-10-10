# Git and Delivery Record

This document preserves the repository's lightweight delivery conventions. The project plan itself was closed in the 2026-10-10 final snapshot; no follow-on release or implementation phase is scheduled.

## Branches

- `main` is the stable integration branch.
- Use short-lived `feature/<name>` branches for planned work.
- Use `fix/<name>` for focused defects and `docs/<name>` for documentation-only work.

The repository may currently be developed directly on `main` while the project is small. Branch policy should become mandatory when parallel work or external contributors begin.

## Commit Style

Use a conventional prefix and a specific scope:

```text
feat(rag): add retrieval threshold
fix(auth): reject expired session
test(api): cover project authorization
docs(control): update delivery status
```

Do not commit secrets, local databases, model files, `target/`, `node_modules/`, or generated personal IDE files.

## Change Workflow

1. State the behavior and affected module.
2. Inspect nearby code and tests.
3. Make a focused change.
4. Run the narrow check, then module checks.
5. Update project-control status when behavior or architecture changes.
6. Review the diff for unrelated files and secrets.
7. Commit and push a stable checkpoint.

## Pull Request Expectations

A meaningful feature should include its tests, setup/configuration changes, API/UI documentation, migration notes, and known limitations. Reviewers should be able to distinguish implemented behavior from roadmap intent without reading private conversation history.

## Release Readiness

The final repository snapshot is a local-demo/portfolio delivery, not a production release. Its frontend build passes; the backend test run recorded two failures, documented in `10-DEVELOPMENT-STATUS.md`. Do not describe this snapshot as having a fully green backend suite or production release readiness.
