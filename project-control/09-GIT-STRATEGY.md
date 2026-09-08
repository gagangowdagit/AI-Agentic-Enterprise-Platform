# Git and Delivery Strategy

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

Use a tag only for a reproducible state with passing relevant checks, documented configuration, migration safety, and a known dependency matrix. The project is currently pre-release and has no production release promise.
