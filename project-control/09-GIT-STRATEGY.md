# Git Strategy

## 1. Purpose

Git will be used to maintain stable development checkpoints, track changes, collaborate safely, and provide rollback capability.

The Git repository is the primary source of truth for the project.

---

## 2. Branch Strategy

### Main Branches

```text
main
develop
```

### Feature Branches

```text
feature/<feature-name>
```

### Bug Fix Branches

```text
fix/<bug-name>
```

### Example

```text
main
  │
  └── develop
        │
        ├── feature/user-authentication
        ├── feature/rag-pipeline
        └── feature/ai-agent
```

---

## 3. Development Workflow

```text
Create Branch
     ↓
Implement Task
     ↓
Test
     ↓
Verify
     ↓
Update Project-Control Files
     ↓
Commit
     ↓
Push
     ↓
Merge
```

---

## 4. Commit Strategy

A completed subtask should normally result in a Git checkpoint.

Example:

```text
feat(user): add user entity
feat(user): add user repository
feat(user): add user service
feat(user): add user controller
test(user): add user CRUD tests
```

Avoid combining unrelated changes in one commit.

---

## 5. Commit Types

Use conventional prefixes:

```text
feat      → New functionality
fix       → Bug fix
test      → Tests
refactor  → Code restructuring
docs      → Documentation
chore     → Maintenance
build     → Build/dependency changes
ci        → CI/CD changes
perf      → Performance improvement
security  → Security changes
```

---

## 6. Commit Rules

Before committing:

* Code compiles.
* Relevant tests pass.
* No unnecessary files are included.
* No secrets are committed.
* Existing functionality is not unnecessarily broken.
* Project-control status is updated when appropriate.

---

## 7. Rollback Strategy

Every stable checkpoint should be recoverable through Git.

```text
Stable Commit A
      ↓
Stable Commit B
      ↓
Stable Commit C
      ↓
New Development
      ↓
Problem
      ↓
Rollback to Stable Commit C
```

Do not rewrite shared history unless there is a clear reason.

---

## 8. Project-Control Updates

Important changes must update the appropriate files:

```text
Development Plan
Requirements
Architecture
Tech Stack
Database Design
API Design
AI Design
Development Status
Decisions
Changelog
```

---

## 9. Pull Requests

Before merging a significant feature:

* Review the changes.
* Run tests.
* Verify no unrelated changes exist.
* Confirm project-control documentation is updated.
* Confirm the feature follows the approved architecture.

---

## 10. Release Tags

Stable releases should use version tags.

Example:

```text
v0.1.0
v0.2.0
v1.0.0
```

Major releases should represent significant stable milestones.

---

## 11. Security

Never commit:

* API keys
* Passwords
* JWT secrets
* AWS credentials
* Database credentials
* Private certificates
* `.env` files containing secrets

Use environment variables and secure secret management instead.
